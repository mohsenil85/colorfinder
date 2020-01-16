package com.lmohseni;

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

}
