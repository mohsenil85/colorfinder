package com.lmohseni;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {

        final String inputFile =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        ImageProcessor.builder() //uses lombok.Builder

            .timeout(1) //idle seconds to wait before shutdown

            .colorCount(3) //per spec

            .quality(1) //tune fidelity vs speed

            .ignoreWhite(true)

            .imageListUrl(inputFile) //url pointing to list of images
                                     // (for a local file, use a path like
                                     // 'file:./some/local.csv')
            .outputFilePath(
                "./target/results.csv"
            ) //where to print the results

            .executorService(
                Executors.newWorkStealingPool()
            ) //what type of threading strategy to use (see benchmarks)

            .localCache(new ConcurrentHashMap<>())
            //use memoization

            .build()

            .processAllImages(); //do it!
    }
}
