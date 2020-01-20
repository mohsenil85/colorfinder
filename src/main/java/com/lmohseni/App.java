package com.lmohseni;


import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {

        final String inputFile =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        ImageProcessor.builder() //uses lombok.Builder

            .timeout(3) //idle seconds to wait before shutting down

            .colorCount(3) //per spec

            .quality(50) //tune the tradeoff between fidelity and speed

            .ignoreWhite(false)

            .imageListUrl(inputFile) //url pointing to list of images
                                     // (for a local file, use a path like
                                     // 'file:./some/local.csv')
            .outputFilePath(
                "./target/results.csv"
            ) //where to print the results

            .executorService(
                Executors.newWorkStealingPool()
            ) //what type of threading strategy to use (see benchmarks)

            .build()

            .processAllImages(); //do it!
    }
}
