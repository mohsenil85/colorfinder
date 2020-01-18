package com.lmohseni;


import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
    static ImageProcessor imageProcessor;
    static File outputFile = new File("./target/results.csv");

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();

        imageProcessor = new ImageProcessor(
            200,
            10,
            1000,
            .9f,
            200,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile
        );

        imageProcessor.init();
        final ConcurrentHashMap<Integer, String[]> results = imageProcessor.processAllImages();
        final int status = imageProcessor.writeOutputFile(results);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        System.out.println("took " + timeElapsed);

        System.out.println("exited with status:" + status);
    }


}
