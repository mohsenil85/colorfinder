package com.lmohseni;


import java.io.File;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int nThreads = 200;
        int timeout = 10;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool(nThreads);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();
        executor.shutdownNow();
    }

}
