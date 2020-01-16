package com.lmohseni;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    BufferedImage downloadImage(String url) throws IOException {
        return ImageIO.read(
            new BufferedInputStream(
                new URL(url).openStream()));
    }

    ColorProcessingResult processImage(String url) {
        try {
            final BufferedImage image = downloadImage(url);
            final HashMap<String, Integer> occurrences = new HashMap<>();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    final String rgb = Integer.toHexString(image.getRGB(x, y)).substring(0,6);
                    if (occurrences.get(rgb) == null) {
                        occurrences.put(rgb, 1);
                    } else {
                        int existing = occurrences.get(rgb);
                        occurrences.put(rgb, existing + 1);
                    }
                }
            }
            Map.Entry<String, Integer> color1 = new AbstractMap.SimpleEntry<>("", 0);
            Map.Entry<String, Integer> color2 = new AbstractMap.SimpleEntry<>("", 0);
            Map.Entry<String, Integer> color3 = new AbstractMap.SimpleEntry<>("", 0);

            for (Map.Entry<String, Integer> entry : occurrences.entrySet()) {
                if (entry.getValue() > color1.getValue()) {
                    color3 = color2;  //propagate runners up, order matters
                    color2 = color1;
                    color1 = entry;
                }
            }

            return new ColorProcessingResult(
                url,
                color1.getKey(),
                color2.getKey(),
                color3.getKey()
            );


        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Not supposed to happen");
    }

    @Data
    class ColorProcessingResult {

        private final String imageUrl;
        private final String color1;
        private final String color2;
        private final String color3;
    }

}
