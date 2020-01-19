package com.lmohseni;


import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {
        ImageProcessor.builder()
            .verbose(true)
            .compressionPercentage(.1f)
            .timeout(10)
            .imageListUrl(
                "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt")
            .outputFilePath("./target/results.csv")
            .executorService(Executors.newWorkStealingPool())
            .build()

            .processAllImages();
    }
}
