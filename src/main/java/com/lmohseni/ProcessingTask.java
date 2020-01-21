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
import java.util.concurrent.Callable;

@Data
@Builder
public class ProcessingTask implements Callable<String[]> {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;

    @NonNull
    private final Map<String, String[]> cache;
    @NonNull
    private final Set<String> dropList;


    @Override
    public String[] call() throws InterruptedException {

        final String[] cached = cache.get(imageUrl);
        if (cached != null) {
            return cached;
        }

        if (dropList.contains(imageUrl)) {
            throw  new InterruptedException("dropping url: " + imageUrl);
        }

        final BufferedImage image = downloadImage();

        if (image != null) {
            final int[][] palette = ColorThief.getPalette(
                image,
                colorCount,
                quality,
                ignoreWhite
            );

            String color1 = convertRgbArrayToHexColor(palette[0]);
            String color2 = convertRgbArrayToHexColor(palette[1]);
            String color3 = convertRgbArrayToHexColor(palette[2]);

            final String[] result = {imageUrl, color1, color2, color3};
            cache.put(imageUrl, result);
            return result;
        } else {

            throw  new InterruptedException("got a null image!");
        }

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
            System.out.printf("malformed url: %s", imageUrl);
            dropList.add(imageUrl);
        } catch (IOException e) {
            System.out.printf("error : %s", e.getMessage());
        }
        return null;
    }

}
