package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

public class ImageProcessorTest {

    final String localTestFilePath = "file:./src/test/resources/test-list.txt";

    ImageProcessor imageProcessor;

    @Before
    public void setUp() throws Exception {
        imageProcessor = new ImageProcessor(localTestFilePath);
    }

    @Test
    public void processAllImages() throws IOException {
        final ConcurrentHashMap<String, String[]> map = imageProcessor
            .processAllImages();
        assertEquals(33, map.size());
    }
}