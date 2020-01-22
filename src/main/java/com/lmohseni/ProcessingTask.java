package com.lmohseni;

import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Data
@Builder
public class ProcessingTask implements Runnable {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;

    @NonNull
    private final Map<String, StringBuilder> cache;
    @NonNull
    private final Set<String> dropList;

    @NonNull
    private final CountDownLatch latch;

    @NonNull
    private final StringBuffer buffer;

    @Override
    public void run() {

        if (dropList.contains(imageUrl)) {
            System.out.printf("ignoring %s%n", imageUrl);
            latch.countDown();
            return;
        }

        final StringBuilder cached = cache.get(imageUrl);
        if (cached != null) {
            buffer.append(cached);
            System.out.printf("cache hit %s", cached.toString());
            latch.countDown();
            return;
        }

        final BufferedImage image;
        try {
            image = downloadImage();
        } catch (IllegalThreadStateException e) {
            System.out.printf("error %s%n", e.getMessage());
            latch.countDown();
            return;
        }

        final int[][] palette = ColorThief.getPalette(
            image,
            colorCount,
            quality,
            ignoreWhite
        );

        StringBuilder result = new StringBuilder().append(imageUrl);

        for (int i = 0; i < colorCount; i++) {
            result.append(",");
            result.append(convertRgbArrayToHexColor(palette[i]));
        }
        result.append("\n");

        buffer.append(result);
        cache.put(imageUrl, result);
        System.out.printf("recorded %s", result.toString());
        latch.countDown();


    }

    String convertRgbArrayToHexColor(int[] rgb) {

        return String.format(
            "#%s%s%s",
            Integer.toHexString(rgb[0]),
            Integer.toHexString(rgb[1]),
            Integer.toHexString(rgb[2])
        ).toUpperCase();
    }

    BufferedImage downloadImage() throws IllegalThreadStateException {

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
        throw new IllegalThreadStateException("problem with " + imageUrl);
    }


}
