package com.lmohseni;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ImageProcessorTest {

    final String localTestFilePath = "file:./src/test/resources/test-list.txt";
    final File localTestOutputFile = new File("./src/test/resources/test-results.csv");

    ImageProcessor imageProcessor;

    @Before
    public void setUp() {
        imageProcessor = new ImageProcessor(
            20,
            10,
            TimeUnit.SECONDS,
            localTestFilePath,
            localTestOutputFile
        );
        imageProcessor.init();
    }

    @Test
    public void processAllImages() throws IOException {
        Instant start = Instant.now();

        final HashMap<Integer, String[]> map = imageProcessor
            .processAllImages();
        assertEquals(33, map.size());

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        System.out.println("took " + timeElapsed);
    }

    @Test
    public void writeOutputFile() throws IOException {
        final HashMap<Integer, String[]> map = new HashMap<>();
        map.put(1,
            new String[]{
                "http://i.imgur.com/FApqk3D.jpg"
                ,"ffffff","ffe000","fffeff",
            }
        );
        map.put(2,
            new String[]{
                "http://i.imgur.com/FApqk3D.jpg"
                ,"ffffff","ffe000","fffeff",
            }
        );
        final int status = imageProcessor
            .writeOutputFile(map);
        assertEquals(0, status);
    }

    @Ignore("Test is ignored as a demonstration")
    @Test
    public void imageProcessorE2E() throws IOException {
        final HashMap<Integer, String[]> map = imageProcessor
            .processAllImages();
        final int status = imageProcessor
            .writeOutputFile(map);

        assertEquals(0, status);

    }
}