package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ImageProcessorTest {

    final String localTestFilePath = "file:./src/test/resources/test-list.txt";

    ImageProcessor imageProcessor;

    @Before
    public void setUp() {
        imageProcessor = new ImageProcessor(
            16,
            20,
            TimeUnit.SECONDS,
            100,
            1,
            localTestFilePath
        );
        imageProcessor.init();
    }

    @Test
    public void processAllImages() throws IOException {
        final ConcurrentHashMap<String, String[]> map = imageProcessor
            .processAllImages();
        assertEquals(33, map.size());
    }
}