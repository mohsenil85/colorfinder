package com.lmohseni;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Data
@Builder
public class ImageProcessor {

    //tunables:
    private final boolean verbose;
    private final float compressionPercentage;
    private final int timeout;
    @NonNull
    private final String imageListUrl;
    @NonNull
    private final String outputFilePath;
    @NonNull
    private final ExecutorService executorService;

    public void processAllImages() {

        final CompletionService<String[]> completionService =
            new ExecutorCompletionService<>(
                executorService);

        Instant start = Instant.now();

        final URL imagesUrl;
        try {
            imagesUrl = new URL(imageListUrl);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(imagesUrl.openStream()));
            reader
                .lines()
                .forEach(url -> {
                        if (verbose) {
                            System.out.println("creating thread for url: " + url);
                        }
                        completionService
                            .submit(
                                ProcessingTask.builder()
                                    .imageUrl(url)
                                    .compressionPercentage(compressionPercentage)
                                    .verbose(verbose)
                                    .build()
                            );
                    }
                );
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int idx = 0;
        while (true) {
            try {
                final BufferedWriter writer = new BufferedWriter(
                    new FileWriter(new File(outputFilePath)));
                final Future<String[]> take = completionService.poll(timeout, TimeUnit.SECONDS);
                if (take == null) {
                    if (verbose) {
                        System.out.printf("maximum idle seconds reached: %d\n", timeout);
                    }
                    writer.close();
                    executorService.shutdownNow();
                    break;
                }
                final String[] strings = take.get();
                if (strings != null) {
                    for (String str : strings) {
                        writer.write(str);
                        writer.write(",");
                    }
                    writer.newLine();
                    writer.flush();

                    idx++;
                    if (verbose) {
                        System.out.printf("total records processed: %d\n", idx);
                        Instant finish = Instant.now();
                        long timeElapsed = Duration.between(start, finish).getSeconds();
                        System.out.printf("elapsed time: %d\n", timeElapsed);
                    }

                }

            } catch (InterruptedException | ExecutionException | NullPointerException | IOException e) {
                System.out.println(e.getMessage());

            }
        }

        if (verbose) {
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).getSeconds();
            System.out.println("total time:  " + timeElapsed);

        }

    }


}
