package com.lmohseni;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

    private CompletionService<ProcessingTask.Result> completionService;

    public void processAllImages() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant start = Instant.now();

        final URL imagesUrl;

        completionService = new ExecutorCompletionService<>(executorService);

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

        final List<String> urls = reader.lines().parallel().collect(Collectors.toList());
        urls.parallelStream()
            .forEach(url -> {

                completionService.submit(
                    ProcessingTask.builder()
                        .imageUrl(url)
                        .colorCount(colorCount)
                        .quality(quality)
                        .ignoreWhite(ignoreWhite)
                        .cache(cache)
                        .dropList(dropList)
                        .outputFile(outputFile)
                        .build()
                );
            });
        int successful = 0;
        int failed = 0;
        while (true) {
            try {
                final Future<ProcessingTask.Result> future = completionService
                    .poll(timeout, TimeUnit.SECONDS);
                if (null == future) {
                    executorService.shutdown();
                    break;
                }
                final ProcessingTask.Result result = future.get();
                if (result.success) {
                    successful++;
                } else {
                    failed++;
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("processed %d successful and %d failed records%n", successful, failed);

        Instant end = Instant.now();
        System.out.printf(
            "execution time: %s s%n", Duration.between(start, end).getSeconds()
        );
        System.out.printf("drop list length: %d %n", dropList.size());

    }

}
