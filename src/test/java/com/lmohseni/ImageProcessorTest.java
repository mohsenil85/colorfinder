package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    private String inputFile =
        "file:./src/test/resources/test-list.txt";

    private String outputFile =
        "./src/test/resources/test-results.csv";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        imageProcessor = ImageProcessor.builder()
            .colorCount(3)
            .quality(10)
            .ignoreWhite(true)
            .enableCache(true)
            .inputFile(inputFile)
            .outputFile(outputFile)
            .build();

    }

    @Test
    public void processAllImages() throws IOException {
        imageProcessor
            .processAllImages();

    }

}