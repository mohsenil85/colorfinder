package com.lmohseni;

import lombok.Data;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProcessingTask implements Runnable {

    @NonNull
    private final String imageUrl;
    @NonNull
    private final ConcurrentHashMap<String, String[]> resultsMap;

    @Override
    public void run() {
        final BufferedImage image = downloadImage();
        final HashMap<String, Integer> occurrences = getColorOccurrences(image);
        final String[] mostPrevalentColors = determineMostPrevalentColors(occurrences);

        resultsMap.put(imageUrl, mostPrevalentColors);

    }

    BufferedImage downloadImage() {
        final URL url;
        try {
            url = new URL(imageUrl);
            return ImageIO.read(
                new BufferedInputStream(url.openStream()));
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalThreadStateException("Had a problem downloading url: " + imageUrl);
        }
    }

    HashMap<String, Integer> getColorOccurrences(BufferedImage image) {
        if (image == null) {
            throw new IllegalThreadStateException("got a null image from url:" + imageUrl);

        }

        final HashMap<String, Integer> occurrences = new HashMap<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final String rgb = convertToRgbHex(image.getRGB(x, y));
                if (occurrences.get(rgb) == null) {
                    occurrences.put(rgb, 1);
                } else {
                    occurrences.put(rgb, occurrences.get(rgb) + 1);
                }
            }
        }
        return occurrences;
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
        return new String[]{color1.getKey(), color2.getKey(), color3.getKey()};

    }

    private String convertToRgbHex(int rgbInt) {
        return Integer.toHexString(rgbInt)
            .substring(0, 6); //ignore alpha channel if it exists
    }


}
