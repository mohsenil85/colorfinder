package com.lmohseni;

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

    BufferedInputStream downloadImage(String url) throws IOException {

        return new BufferedInputStream(new URL(url).openStream());

    }

}
