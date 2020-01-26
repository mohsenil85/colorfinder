package com.lmohseni;

import co.paralleluniverse.common.util.Pair;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;
import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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

    private final Map<URL, String> cache = new ConcurrentHashMap<>();
    private final Set<URL> dropList = ConcurrentHashMap.newKeySet();

    private BufferedReader reader;
    private BufferedWriter writer;
    private int batchSize;


    @SneakyThrows
    public void processAllImages() {

        System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant start = Instant.now();

        try {
            new URL(inputFile);
        } catch (MalformedURLException e1) {
            throw new RuntimeException("could not create a url from: " + inputFile);
        }

        try {
            final InputStream is = new URL(inputFile).openStream();
            reader = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e1) {
            throw new RuntimeException("could not read from: " + inputFile);
        }

        writer = Files.newBufferedWriter(Path.of(outputFile));

        reader
            .lines()
            .parallel()
            .map(line -> tryCreateUrl(line))
            .filter(Objects::nonNull)
            .filter(url -> !dropList.contains(url))
            .map(url -> downloadAndSummarize(url))
            .filter(Objects::nonNull)
            .forEach(result -> writeResult(result.toString()));

        System.out.println("exiting");

//        writer.flush();

        Instant end = Instant.now();
        System.out.printf(
            "execution time: %s s%n", Duration.between(start, end).getSeconds()
        );
        final long records = getFileLength(outputFile);
        System.out.printf("processed %d records%n", records);
        System.out.printf("drop list length: %d %n", dropList.size());
        System.out.printf("cache size %d records%n", cache.size());


    }

    @SneakyThrows(IOException.class)
    long getFileLength(String outputFile) {
        return Files.lines(Paths.get(outputFile)).count();
    }


    @Suspendable
    void writeResult(String message) {
        try {
            new Fiber<Void>((SuspendableRunnable) () -> {
                try {
                    writer.write(message);
                    writer.flush();
                    System.out.printf("wrote %s", message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start().join();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Suspendable
    private String downloadAndSummarize(URL url) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        try {
            return new Fiber<>((SuspendableCallable<String>) () -> {
                final Pair<URL, BufferedImage> data = downloadImage(url);
                if (data != null && data.getSecond() != null) {

                    final String palette = detectPalette(data, colorCount, quality, ignoreWhite);
                    if (palette != null) {
                        cache.put(url, palette);
                        return palette;
                    }
                }
                return null;

            }).start().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Suspendable
    private String detectPalette(
        Pair<URL, BufferedImage> data,
        int colorCount,
        int quality,
        boolean ignoreWhite
    ) {
        final URL url = data.getFirst();
        final BufferedImage image = data.getSecond();
        try {
            return new Fiber<String>(
                (SuspendableCallable<String>) () -> {

                    final int[][] palette = ColorThief.getPalette(
                        image,
                        colorCount,
                        quality,
                        ignoreWhite
                    );

                    final StringBuilder result = new StringBuilder();

                    result.append(url.toString());

                    for (int i = 0; i < colorCount; i++) {
                        result.append(",");
                        result.append(convertRgbArrayToHexColor(palette[i]));
                    }
                    result.append("\n");
                    return result.toString();

                }).start().get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("fiber problem: " + url.toString());
            e.printStackTrace();
        }
        return null;

    }

    String convertRgbArrayToHexColor(int[] rgb) {

        return String.format(
            "#%s%s%s",
            Integer.toHexString(rgb[0]),
            Integer.toHexString(rgb[1]),
            Integer.toHexString(rgb[2])
        ).toUpperCase();
    }


    @Suspendable
    private Pair<URL, BufferedImage> downloadImage(URL url) {

        try {
            return new Fiber<>(
                (SuspendableCallable<Pair<URL, BufferedImage>>) () -> {
                    try {
                        final InputStream inputStream = url.openStream();
                        if (inputStream != null) {
                            final BufferedInputStream is = new BufferedInputStream(
                                inputStream);
                            final BufferedImage image = ImageIO.read(is);
                            if (image != null) {
                                return new Pair<URL, BufferedImage>(url, image);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.printf("adding %s to droplist%n", url.toString());
                        dropList.add(url);
                    } catch (IOException e) {
                        System.out.printf("problem with url: %s%n", url.toString());

                    }
                    return null;

                }).start().get();

        } catch (ExecutionException | InterruptedException e) {
            System.out.println("fiber problem: " + url.toString());
            e.printStackTrace();

        }
        return null;
    }

    private URL tryCreateUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            System.out.println("malformed url");
            return null;
        }
    }
}
