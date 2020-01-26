package com.lmohseni;

import co.paralleluniverse.common.util.Pair;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;
import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.NonNull;

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

    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;
    private final boolean enableCache;
    @NonNull
    private final String inputFile;
    @NonNull
    private final String outputFile;

    BufferedReader reader;
    BufferedWriter writer;

    Map<URL, String> cache;
    Set<URL> dropList;

    public void processAllImages() {

        Instant start = Instant.now();
        System.setProperty("co.paralleluniverse.fibers.detectRunawayFibers", "false");

        reader = tryCreateReader(tryCreateUrl(inputFile));
        writer = tryCreateWriter(outputFile);

        cache = new ConcurrentHashMap<>();
        dropList = ConcurrentHashMap.newKeySet();

        reader
            .lines()
            .parallel()
            .map(this::tryCreateUrl)
            .filter(Objects::nonNull)
            .filter(url -> !dropList.contains(url))
            .map(url -> processOneImage(url, cache, dropList, enableCache))
            .filter(Objects::nonNull)
            .forEach(result -> writeResult(result, writer));

        Instant end = Instant.now();

        System.out.printf("execution time: %s%n", executionTime(start, end));
        System.out.printf("processed %d records%n", getFileLength(outputFile));
        System.out.printf("drop list length: %d%n", dropList.size());
        System.out.printf("cache size %d records%n", cache.size());

    }

    @Suspendable
    String processOneImage(URL url, Map<URL, String> cache, Set<URL> dropList,
        boolean enableCache) {
        try {
            return new Fiber<>((SuspendableCallable<String>) () -> {

                if (enableCache && cache.containsKey(url)) {
                    return cache.get(url);
                }
                final Pair<URL, BufferedImage> data = downloadImage(url, dropList);
                if (data != null) {
                    final String palette = detectPalette(data, cache);
                    if (enableCache) {
                        cache.put(url, palette);
                    }
                    return palette;
                }
                return null;

            }).start().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    Pair<URL, BufferedImage> downloadImage(URL url, Set<URL> dropList) {
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
    }

    String detectPalette(Pair<URL, BufferedImage> data, Map<URL, String> cache) {
        final URL url = data.getFirst();
        final BufferedImage image = data.getSecond();
        final int[][] palette = ColorThief.getPalette(
            image,
            colorCount,
            quality,
            ignoreWhite
        );

        return formatResult(url, palette);
    }

    @Suspendable
    void writeResult(String message, BufferedWriter writer) {
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

    String formatResult(URL url, int[][] palette) {
        final StringBuilder result = new StringBuilder();

        result.append(url.toString());

        for (int i = 0; i < colorCount; i++) {
            result.append(",");
            result.append(convertRgbArrayToHexColor(palette[i]));
        }
        result.append("\n");
        return result.toString();

    }

    String convertRgbArrayToHexColor(int[] rgb) {

        return String.format(
            "#%s%s%s",
            Integer.toHexString(rgb[0]),
            Integer.toHexString(rgb[1]),
            Integer.toHexString(rgb[2])
        ).toUpperCase();
    }

    private URL tryCreateUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            System.out.printf("malformed url: %s%n", s);
            return null;
        }
    }

    private BufferedReader tryCreateReader(URL inputUrl) {
        if (inputUrl == null) {
            throw new RuntimeException("null input url");
        }
        try {
            final InputStream is = inputUrl.openStream();
            return new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            throw new RuntimeException("could not read from: " + inputUrl.toString());
        }
    }

    private BufferedWriter tryCreateWriter(String outputFile) {
        try {
            return Files.newBufferedWriter(Path.of(outputFile));
        } catch (IOException e) {
            throw new RuntimeException("could not write to: " + outputFile);
        }
    }

    private long executionTime(Instant start, Instant end) {
        return Duration.between(start, end).getSeconds();

    }

    private long getFileLength(String outputFile) {
        try {
            return Files.lines(Paths.get(outputFile)).count();
        } catch (IOException e) {
            throw new RuntimeException("error writing to: " + outputFile);
        }
    }

}
