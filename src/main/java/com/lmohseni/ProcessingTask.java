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

@Data
public class ProcessingTask implements Runnable {

    private final String imageUrl;


    @Override
    public void run() {
        System.out.println("my image url is: " + imageUrl );

    }

    BufferedImage downloadImage() throws IOException {
        if (imageUrl != null){
            return ImageIO.read(
                new BufferedInputStream(
                    new URL(imageUrl).openStream()));
        }
        throw new RuntimeException("Thread was created with null url");
    }

    ColorProcessingResult processImage() {
        try {
            final BufferedImage image = downloadImage();
            final HashMap<String, Integer> occurrences = getColorOccurrences(image);
            final String[] mostPrevalentColors = determineMostPrevalentColors(occurrences);

            return new ColorProcessingResult(
                imageUrl,
                mostPrevalentColors[0],
                mostPrevalentColors[1],
                mostPrevalentColors[2]
            );


        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Not supposed to happen");
    }

    private HashMap<String, Integer> getColorOccurrences(BufferedImage image) {

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
