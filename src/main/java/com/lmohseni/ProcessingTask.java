package com.lmohseni;

import lombok.Data;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Data
public class ProcessingTask implements Callable<String[]> {

    @NonNull
    private final String imageUrl;

    @NonNull
    private final float compressionPercentage;

    private final String troublesomeUlr = "https://i.redd.it/nrafqoujmety.jpg";

    @Override
    public String[] call() {
        Instant start = Instant.now();

        if (imageUrl.equals(troublesomeUlr)) return null;
        final BufferedImage image = downloadImage();
        if (image != null) {
            final BufferedImage scaled = resizeImage(image, compressionPercentage);
            final HashMap<String, Integer> occurrences = getColorOccurrences(scaled);
            final String[] strings = determineMostPrevalentColors(occurrences);
            System.out.println(Arrays.toString(strings));
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).getSeconds();
            System.out
                .println(Thread.currentThread().getName() + " took " + timeElapsed + " seconds.");
            return strings;
        }
        return null;
    }


    BufferedImage downloadImage() {
        final URL url;
        try {
            url = new URL(imageUrl);
            if (url != null) {
                return ImageIO.read(
                    new BufferedInputStream(url.openStream()));

            }
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalThreadStateException("Problem downloading image from: " + imageUrl);
        }
        return null;
    }

    HashMap<String, Integer> getColorOccurrences(BufferedImage image) {
        if (image == null) {
            throw new IllegalThreadStateException("got a null image from url:" + imageUrl);
        }

        final HashMap<String, Integer> occurrences = new HashMap<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final String rgb = convertToRgbHex(image.getRGB(x, y));
                occurrences.merge(rgb, 1, Integer::sum);
            }
        }
        return occurrences;
    }

    BufferedImage resizeImage(BufferedImage inputImage, float percent) {
        if (inputImage != null) {
            int scaledWidth = (int) (inputImage.getWidth() * percent);
            int scaledHeight = (int) (inputImage.getHeight() * percent);

            BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            return outputImage;
        }
        return null;
    }

    private String[] determineMostPrevalentColors(HashMap<String, Integer> map) {

        Map.Entry<String, Integer> color1 = new AbstractMap.SimpleEntry<>("", 0);
        Map.Entry<String, Integer> color2 = new AbstractMap.SimpleEntry<>("", 0);
        Map.Entry<String, Integer> color3 = new AbstractMap.SimpleEntry<>("", 0);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > color1.getValue()) {
                color3 = color2;  //propagate runners up, order matters
                color2 = color1;
                color1 = entry;
            }
        }
        return new String[]{imageUrl, color1.getKey(), color2.getKey(), color3.getKey()};
    }

    private String convertToRgbHex(int rgbInt) {
        String result = Integer.toHexString(rgbInt)
            .toUpperCase()
            .substring(0, 6); //ignore alpha channel if it exists
        return "#" + result;
    }


}
