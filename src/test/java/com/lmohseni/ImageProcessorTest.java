package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ImageProcessorTest {

    final String localTestFilePath = "file:./src/test/resources/test-list.txt";

    ImageProcessor imageProcessor;

    @Before
    public void setUp() {
        imageProcessor = new ImageProcessor(
            20,
            5,
            TimeUnit.SECONDS,
            localTestFilePath
        );
        imageProcessor.init();
    }

    @Test
    public void processAllImages() throws IOException {
        final HashMap<Integer, String[]> map = imageProcessor
            .processAllImages();
        assertEquals(33, map.size());
    }
}