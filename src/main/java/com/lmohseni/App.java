package com.lmohseni;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


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
            final int width = image.getWidth();
            final int height = image.getHeight();
            int[] pixels = new int[width*height];
            int idx = 0;
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    final int rgb = image.getRGB(x, y);
                    pixels[idx] = rgb;
                    idx++;
                }
            }

            Arrays.sort(pixels);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Data
    class ColorProcessingResult {

        String imageUrl;
        Color color1;
        Color color2;
        Color color3;
    }

}
