package com.lmohseni;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    @Mock
    private ConcurrentHashMap<String, String[]> resultsMap;
    @Mock
    private CompletionService<String[]> completionService;
    final String localTestFilePath = "file:./src/test/resources/test-list.txt";
    final File localTestOutputFile = new File("./src/test/resources/test-results.csv");


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        imageProcessor = new ImageProcessor(
            5,
            TimeUnit.SECONDS,
            localTestFilePath,
            localTestOutputFile,
            completionService,
            .5f
        );
    }

    @Test
    public void processAllImages() throws IOException, ExecutionException, InterruptedException {

        imageProcessor
            .processAllImages();

    }

    @Test
    @Ignore("Used for benchmarking")
    public void imageProcessorE2E() throws IOException, ExecutionException, InterruptedException {
        Instant start = Instant.now();

        imageProcessor.processAllImages();

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).getSeconds();
        System.out.println("elapsed time: " + timeElapsed);


    }
}