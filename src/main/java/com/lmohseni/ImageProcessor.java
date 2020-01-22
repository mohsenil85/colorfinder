package com.lmohseni;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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
    private final Map<String, StringBuilder> cache;
    @NonNull
    private final Set<String> dropList;

    private BufferedReader reader;
    private BufferedWriter writer;

    private int recordsCount;
    private StringBuffer buffer;

    public void processAllImages() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant start = Instant.now();

        initialize();
        final List<String> urls = reader.lines().parallel().collect(Collectors.toList());
        final CountDownLatch latch = new CountDownLatch(urls.size());
        urls.parallelStream()
            .forEach(url -> launchThread(url, latch, buffer));
        try {
            latch.await();
            cleanUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();
        System.out.printf(
            "execution time: %s s%n", Duration.between(start, end).getSeconds()
        );
        System.out.printf("drop list length: %d %n", dropList.size());

    }

    private void initialize() {

        final URL imagesUrl;
        buffer = new StringBuffer();

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

    private void launchThread(
        final String url,
        final CountDownLatch latch,
        final StringBuffer buffer
    ) {

        System.out.printf("launching %s%n", url);

        executorService.submit(
            ProcessingTask.builder()
                .imageUrl(url)
                .colorCount(colorCount)
                .quality(quality)
                .ignoreWhite(ignoreWhite)
                .cache(cache)
                .dropList(dropList)
                .latch(latch)
                .buffer(buffer)
                .build()
        );
    }

    @SneakyThrows
    private void cleanUp() {

        executorService.shutdown();
        writer.write(buffer.toString());

//        Thread.sleep(timeout * 1000);
        writer.flush();
        executorService.awaitTermination(timeout, TimeUnit.SECONDS);

        reader.close();
        writer.close();

    }

}
