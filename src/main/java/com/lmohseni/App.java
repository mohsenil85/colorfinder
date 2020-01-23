package com.lmohseni;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class App {


    public static void main(String[] args) {
        //TODO: inputFile = args[1]

        final String inputFile =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        createDefaultImageProcessor(inputFile).processAllImages();

    }

    private static ImageProcessor createDefaultImageProcessor(final String inputFile){
        return ImageProcessor.builder()

            //idle seconds to wait before shutdown
            .timeout(1)

            //per spec
            .colorCount(3)

            //tune fidelity vs speed
            .quality(10)
            .ignoreWhite(true)

            //url pointing to list of images
            // (for a local file, use a path like 'file:./some/local.csv')
            .inputFile(inputFile)

            //where to print the results
            .outputFile("./target/results.csv")

            //threading strategy to use (see benchmarks)
            .executorService(Executors.newWorkStealingPool())

            //use memoization
            .cache(new ConcurrentHashMap<>())

            //don't retry bad operations
            .dropList(ConcurrentHashMap.newKeySet())

            .build();

    }
}
