package com.lmohseni;


public class App {


    public static void main(String[] args) {

        final String inputFile =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        createDefaultImageProcessor(inputFile).processAllImages();

    }

    private static ImageProcessor createDefaultImageProcessor(final String inputFile) {
        return ImageProcessor.builder()
            .colorCount(3)
            .quality(10)
            .ignoreWhite(false)
            .enableCache(true)
            .inputFile(inputFile)
            .outputFile("./target/results.csv")
            .build();

    }
}
