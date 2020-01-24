package com.lmohseni;

import co.paralleluniverse.strands.SuspendableRunnable;
import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Data
@Builder
public class ProcessingTask implements SuspendableRunnable {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;

    @NonNull
    private final Map<String, String> cache;
    @NonNull
    private final Set<String> dropList;

    @NonNull
    private final String outputFile;


    @NonNull
    private final CountDownLatch latch;

    @NonNull
    private final BufferedWriter writer;


    @SneakyThrows
    @Override
    public void run() {

        latch.countDown();

//        Lock lock = new ReentrantLock();

        final String name = Thread.currentThread().getName();

        if (dropList.contains(imageUrl)) {
            System.out.printf("%s: ignoring %s%n", name, imageUrl);
            return;
        }

        final String cached = cache.get(imageUrl);
        if (cached != null) {
            System.out.printf("%s: cache hit %s", name, cached);
            writer.write(cached);
            return;
        }
        final BufferedImage image = downloadImage();

        if (null == image) {
            System.out.printf("%s, problem with: %s%n", name, imageUrl);
            return;
        }

        String result = constructResult(image);
//        writeLn(result);

        cache.put(imageUrl, result);

        System.out.printf("%s recorded %s", name, result);

        writer.write(result);



//        writeLn(result);
    }

    @SneakyThrows
    private void writeLn(String s) {
        System.out.println("ATTEMPTING TO WRITE");
        Files.write(Path.of(outputFile),
            s.getBytes(),
            StandardOpenOption.APPEND
        );
    }


    private String constructResult(BufferedImage image) {
        final StringBuilder result = new StringBuilder();
        final int[][] palette = ColorThief.getPalette(
            image,
            colorCount,
            quality,
            ignoreWhite
        );

        result.append(imageUrl);

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

    BufferedImage downloadImage() {

        try {

            final URL url = new URL(imageUrl);
            final InputStream inputStream = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(
                new BufferedInputStream(inputStream));
            return bufferedImage;

        } catch (FileNotFoundException e) {
            System.out.printf("adding %s to ignore list%n", imageUrl);
            dropList.add(imageUrl);
        } catch (MalformedURLException e) {
            System.out.printf("malformed url: %s%n", imageUrl);
            dropList.add(imageUrl);
        } catch (IOException e) {
            System.out.printf("error : %s%n", e.getMessage());
        }
        return null;
    }

    @Data
    @Builder
    static class Result {

        final String url;
        final boolean success;

    }


}
