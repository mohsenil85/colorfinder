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
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Data
@Builder
public class ImageProcessor {

    private final int timeout;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;
    @NonNull
    private final String imageListUrl;
    @NonNull
    private final String outputFilePath;
    @NonNull
    private final ExecutorService executorService;

    CompletionService<String[]> completionService;
    URL imagesUrl;
    BufferedReader reader;
    BufferedWriter writer;
    int recordsCount = 0;


    public void processAllImages() {

        final Instant start = Instant.now();

        initialize();
        reader
            .lines()
            .forEach(url -> launchThread(url));
        collectResults();
        cleanUp();

        final Instant finish = Instant.now();
        System.out.printf("execution time: %s seconds\n", Duration.between(start, finish).getSeconds());

    }

    private void initialize() {

        try {

            completionService = new ExecutorCompletionService<>(
                executorService);

            try {
                imagesUrl = new URL(imageListUrl);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("could not create a url from: " + imageListUrl);
            }

            try {
                reader = new BufferedReader(
                    new InputStreamReader(imagesUrl.openStream()));
            } catch (IOException e) {
                throw new IllegalStateException("could not read from: " + imageListUrl);
            }

            try {
                writer = new BufferedWriter(
                    new FileWriter(new File(outputFilePath)));
            } catch (IOException e) {
                throw new IllegalStateException("could not write at: " + outputFilePath);
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void launchThread(String url) {

        System.out.printf("launching a thread for %s\n", url);

        completionService.submit(
            ProcessingTask.builder()
                .imageUrl(url)
                .colorCount(colorCount)
                .quality(quality)
                .ignoreWhite(ignoreWhite)
                .build()
        );
    }

    private void collectResults() {

        while (true) {
            try {

                final Future<String[]> take =
                    completionService.poll(timeout, TimeUnit.SECONDS);

                if (take == null) {
                    break;
                }

                recordResults(take.get());

            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.printf("processed %d records\n", recordsCount);
    }

    private void recordResults(String[] strings) {

        try {

            if (strings != null) {
                final String result = format("%s,%s,%s,%s\n", strings);
                System.out.printf("recording result:\n%s\n", result);
                writer.write(result);
                writer.flush();
                //flush after each write so that if we get
                // interrupted, we still can save all the
                // results collected so far
                recordsCount++;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cleanUp() {

        try {

            executorService.shutdownNow();
            reader.close();
            writer.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
