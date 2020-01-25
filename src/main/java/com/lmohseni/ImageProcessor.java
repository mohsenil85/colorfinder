package com.lmohseni;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
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
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
    private int batchSize;


    @SneakyThrows
    public void processAllImages() {

//        System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");

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

        final List<String> urls = reader.lines().collect(Collectors.toList());

        FiberExecutorScheduler scheduler = new FiberExecutorScheduler(
            "img-proc-pool",
            executorService
        );
        batchSize = urls.size();

        final List<ProcessingTask> tasks = urls.stream()
            .map(url -> createTask(url))
            .collect(Collectors.toList());

        final List<Fiber<ProcessingTask.Result>> fibers = tasks.stream()
            .map(task -> {
                return new Fiber<>(scheduler, task).start();
            }).collect(Collectors.toList());

        for (Fiber<ProcessingTask.Result> f : fibers) {
            try {
                f.join();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            throw new InterruptedException();
        } catch (Exception e) {
            System.out.println("exiting");

            writer.flush();
            executorService.shutdownNow();

            Instant end = Instant.now();
            System.out.printf(
                "execution time: %s s%n", Duration.between(start, end).getSeconds()
            );
            try {
                final long records = Files.lines(Paths.get(outputFile)).count();
                System.out.printf("processed %d records%n", records);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.printf("drop list length: %d %n", dropList.size());
            System.out.printf("cache size %d records%n", cache.size());

        }


    }


    private ProcessingTask createTask(
        String url) {
        return
            ProcessingTask.builder()
                .imageUrl(url)
                .writer(writer)
                .colorCount(colorCount)
                .quality(quality)
                .ignoreWhite(ignoreWhite)
                .cache(cache)
                .dropList(dropList)
                .outputFile(outputFile)
                .size(batchSize)
                .build();

    }

}
