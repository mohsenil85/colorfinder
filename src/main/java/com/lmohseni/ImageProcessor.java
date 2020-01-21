package com.lmohseni;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    private final Map<String, String[]> cache;
    @NonNull
    private final Set<String> dropList;

    @Setter
    private CompletionService<String[]> completionService;
    @Setter
    private BufferedReader reader;
    @Setter
    private BufferedWriter writer;

    private int recordsCount;

    public void processAllImages() {

        Instant start = Instant.now();

        initialize();
        reader
            .lines()
            .forEach(url -> launchThread(url));
        collectResults();
        cleanUp();

        Instant end = Instant.now();
        System.out.printf(
            "execution time: %s s%n", Duration.between(start, end).getSeconds()
        );
        System.out.printf("drop list length: %d %n", dropList.size());

    }

    private void initialize() {

        final URL imagesUrl;

        completionService = new ExecutorCompletionService<>(
            executorService);

        try {
            imagesUrl = new URL(inputFile);
        } catch (MalformedURLException e) {
            throw new RuntimeException("could not create a url from: " + inputFile);
        }

        try {
            reader = new BufferedReader(
                new InputStreamReader(imagesUrl.openStream()));
        } catch (IOException e) {
            throw new RuntimeException("could not read from: " + inputFile);
        }

        try {
            writer = new BufferedWriter(
                new FileWriter(new File(outputFile)));
        } catch (IOException e) {
            throw new RuntimeException("could not write at: " + outputFile);
        }

    }

    private void launchThread(String url) {

        System.out.printf("launching a thread for %s%n", url);

        completionService.submit(
            ProcessingTask.builder()
                .imageUrl(url)
                .colorCount(colorCount)
                .quality(quality)
                .ignoreWhite(ignoreWhite)
                .cache(cache)
                .dropList(dropList)
                .build()
        );
    }

    private void collectResults() {

        while (true) {
            try {

                final Future<String[]> future =
                    completionService.poll(timeout, TimeUnit.SECONDS);
                //blocking call, will return null after `timeout` seconds

                if (future == null) {
                    //if we reach the timeout, it means the completion service
                    // is done receiving results, so exit the loop
                    break;
                }

                //unwrap future
                recordResults(future.get());

            } catch (InterruptedException | ExecutionException e) {
                //need to catch these inside the loop
                System.out.println(e.getMessage());
            }
        }

        System.out.printf("processed %d records%n", recordsCount);
    }

    @SneakyThrows
    private void recordResults(String[] strings) {

        System.out.printf("recording : %s %s,%s,%s%n ", strings);
        writer.write(String.format("%s,%s,%s,%s%n", strings));
        writer.flush();
        //flush after each write so that if we get
        // interrupted, we still can save all the
        // results collected so far
        recordsCount++;

    }

    @SneakyThrows
    private void cleanUp() {

        executorService.shutdownNow();
        reader.close();
        writer.close();

    }

}
