package com.lmohseni;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
    static ImageProcessor imageProcessor;
    static File outputFile = new File("/target/results.csv");

    public static void main(String[] args) throws IOException {
        imageProcessor = new ImageProcessor(
            20,
            10,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile
        );
        imageProcessor.init();
        final HashMap<Integer, String[]> results = imageProcessor.processAllImages();
        final int status = imageProcessor.writeOutputFile(results);
        System.out.println("exited with status:" + status);
    }


}
