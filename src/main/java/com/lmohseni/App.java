package com.lmohseni;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    BufferedImage downloadImage(String url) throws IOException {
        return ImageIO.read(
            new BufferedInputStream(
                new URL(url).openStream()));
    }

    ColorProcessingResult processImage(BufferedImage image) {
        return null;
    }

    @Data
    class ColorProcessingResult {

        String imageUrl;
        int color1;
        int color2;
        int color3;
    }

}
