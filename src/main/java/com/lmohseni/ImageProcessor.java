package com.lmohseni;

import co.paralleluniverse.fibers.DefaultFiberScheduler;
import co.paralleluniverse.fibers.Fiber;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class ImageProcessor {

    private final int timeout;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;
    @NonNull
    private final String inputFile;
    @NonNull
    private final String outputFile;
    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final Map<String, String> cache;
    @NonNull
    private final Set<String> dropList;

    private BufferedReader reader;
    private BufferedWriter writer;

    private CompletionService<ProcessingTask.Result> completionService;

    public void processAllImages() {

        System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant start = Instant.now();

        final URL imagesUrl;



        try {
            imagesUrl = new URL(inputFile);
        } catch (MalformedURLException e1) {
            throw new RuntimeException("could not create a url from: " + inputFile);
        }

        try {
            reader = new BufferedReader(
                new InputStreamReader(imagesUrl.openStream()));
        } catch (IOException e1) {
            throw new RuntimeException("could not read from: " + inputFile);
        }

        try {
            writer = new BufferedWriter(
                new FileWriter(outputFile)
            );
        } catch (IOException e) {
            throw new RuntimeException("could not write: " + outputFile);
        }

        final List<String> urls = reader.lines().parallel().collect(Collectors.toList());

        CountDownLatch latch = new CountDownLatch(urls.size());
        final List<ProcessingTask> tasks = urls.stream()
            .map((String url) -> createTask(url,latch,writer))
            .collect(Collectors.toList());

        DefaultFiberScheduler scheduler = new DefaultFiberScheduler();

         tasks
            .forEach(task -> {
                new Fiber<>(task).start();

            });

        try {
            latch.await(10, TimeUnit.SECONDS);
            Thread.sleep(1000);
            writer.flush();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

//        fibers.forEach(stringFiber -> {
//            try {
//                System.out.println("FOOO");
//                final String result = stringFiber.get();
////                final String result = stringFiber.joinNoSuspend().get(1, TimeUnit.SECONDS);
//                if (result != null) {
//                    writeLn(result);
//                }
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

//        fibers.forEach(f -> {
//            try {
//                f.join(10, TimeUnit.SECONDS);
//            } catch (ExecutionException | InterruptedException | TimeoutException e) {
//                e.printStackTrace();
//            }
//        });
//        int successful = 0;
//        int failed = 0;
//        while (true) {
//            try {
//                final Future<ProcessingTask.Result> future = completionService
//                    .poll(timeout, TimeUnit.SECONDS);
//                if (null == future) {
//                    executorService.shutdown();
//                    break;
//                }
//                final ProcessingTask.Result result = future.get();
//                if (result.success) {
//                    successful++;
//                } else {
//                    failed++;
//                }
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }

//        System.out.printf("processed %d successful and %d failed records%n", successful, failed);

        Instant end = Instant.now();
        System.out.printf(
            "execution time: %s s%n", Duration.between(start, end).getSeconds()
        );
        System.out.printf("drop list length: %d %n", dropList.size());

    }


    private ProcessingTask createTask(
        String url,
        CountDownLatch latch,
        BufferedWriter writer
        ) {
        return
            ProcessingTask.builder()
                .imageUrl(url)
                .latch(latch)
                .writer(writer)
                .colorCount(colorCount)
                .quality(quality)
                .ignoreWhite(ignoreWhite)
                .cache(cache)
                .dropList(dropList)
                .outputFile(outputFile)
                .build();

    }

}
