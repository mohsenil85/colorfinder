package com.lmohseni;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {

        final String inputFile =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        ImageProcessor.builder() //uses lombok.Builder

            .timeout(3) //idle seconds to wait before shutdown

            .colorCount(3) //per spec

            .quality(10) //tune fidelity vs speed

            .ignoreWhite(true)

            .inputFile(inputFile) //url pointing to list of images
                                     // (for a local file, use a path like
                                     // 'file:./some/local.csv')
            .outputFile(
                "./target/results.csv"
            ) //where to print the results

            .executorService(
                Executors.newWorkStealingPool(5)
            ) //threading strategy to use (see benchmarks)

            .cache(new ConcurrentHashMap<>())
            //use memoization

            .dropList(ConcurrentHashMap.newKeySet())

            .build()

            .processAllImages(); //do it!
    }
}
