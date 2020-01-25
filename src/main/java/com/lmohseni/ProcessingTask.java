package com.lmohseni;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;
import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Data
@Builder
public class ProcessingTask implements SuspendableCallable<ProcessingTask.Result> {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;
    private final int size;

    @NonNull
    private final Map<String, String> cache;
    @NonNull
    private final Set<String> dropList;

    @NonNull
    private final String outputFile;

    @NonNull
    private final BufferedWriter writer;

    String threadName;

    @Override
    @Suspendable
    public Result run() {

        threadName = Strand.currentStrand().getName();

        final Result result;

        if (dropList.contains(imageUrl)) {
            System.out.printf("%s: ignoring %s%n", threadName, imageUrl);
            return Result.builder()
                .message("ignored " + imageUrl)
                .success(true)
                .build();
        }

        final String cached;
        cached = cache.get(imageUrl);
        if (cached != null) {
            writeResult(cached);
            return Result.builder()
                .message("cache hit " + imageUrl)
                .success(true)
                .build();
        }

        final Optional<BufferedImage> image = downloadImage(imageUrl);

        if (image.isPresent()) {
            final String palette = detectPalette(
                image.get(),
                colorCount,
                quality,
                ignoreWhite
            );

            cache.put(imageUrl, palette);

            writeResult(palette);

            result = Result.builder()
                .success(true)
                .message(palette)
                .build();

        } else {

            result = Result.builder()
                .success(false)
                .message("couldn't download: " + imageUrl)
                .build();

        }

        return result;

    }

    @Suspendable
    void writeResult(String message) {

        try {
            new Fiber<Void>((SuspendableRunnable) () -> {
                try {
                    writer.write(message);
                    writer.flush();
                    System.out
                        .printf("l# %s * recorded %s", threadName, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start().join();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Suspendable
    Optional<int[][]> getPalette(
        final BufferedImage image, int colorCount, int quality, boolean ignoreWhite) {
        try {
            final int[][] palette = new Fiber<>((SuspendableCallable<int[][]>) () -> {
                return ColorThief.getPalette(
                    image,
                    colorCount,
                    quality,
                    ignoreWhite
                );
            }).start().get();
            if (palette != null) {
                return Optional.of(palette);
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("fiber error");
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Suspendable
    private String detectPalette(
        BufferedImage image,
        int colorCount,
        int quality,
        boolean ignoreWhite
    ) {

        final Optional<int[][]> palette = getPalette(
            image,
            colorCount,
            quality,
            ignoreWhite
        );

        final StringBuilder result = new StringBuilder();

        result.append(imageUrl);

        for (int i = 0; i < colorCount; i++) {
            result.append(",");
            result.append(convertRgbArrayToHexColor(palette.orElseThrow()[i]));
        }
        result.append("\n");
        return result.toString();


    }

    @Suspendable
    String convertRgbArrayToHexColor(int[] rgb) {

        return String.format(
            "#%s%s%s",
            Integer.toHexString(rgb[0]),
            Integer.toHexString(rgb[1]),
            Integer.toHexString(rgb[2])
        ).toUpperCase();
    }

    @Suspendable
    Optional<BufferedImage> downloadImage(String url) {
        try {
            final BufferedImage image = new Fiber<>((SuspendableCallable<BufferedImage>) () -> {
                try {
                    final InputStream inputStream = new URL(url).openStream();
                    if (inputStream != null) {
                        final BufferedInputStream is = new BufferedInputStream(
                            inputStream);
                        final BufferedImage bufferedImage = ImageIO.read(is);
                        if (bufferedImage != null) {
                            return bufferedImage;
                        }
                    }
                } catch (MalformedURLException | FileNotFoundException e) {
                    System.out.printf("adding %s to droplist%n", url);
                    dropList.add(url);
                } catch (IOException e) {
                    System.out.printf("problem with url: %s%n", url);

                }
                return null;
            }).start().get();
            if (image != null) {
                return Optional.of(image);
            }

        } catch (ExecutionException | InterruptedException e) {
            System.out.println("fiber error");
        }
        return Optional.empty();
    }

    @Data
    @Builder
    static class Result {

        boolean success;
        String message;

    }


}
