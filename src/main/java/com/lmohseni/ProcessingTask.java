package com.lmohseni;

import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

@Data
@Builder
public class ProcessingTask implements Callable<String[]> {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;

    @Override
    public String[] call() {

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

            return new String[]{imageUrl, color1, color2, color3};
        }
        return null;
    }

    private String convertRgbArrayToHexColor(int[] rgb) {

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
        } catch (IllegalArgumentException | IOException e) {
            //if something goes wrong here, just drop everything and return
            //we will handle nulls downstream
            return null;
        }
    }
}
