package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutorService;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    @Mock
    private ExecutorService executorService;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        imageProcessor = ImageProcessor.builder()
            .timeout(5)
            .imageListUrl("file:./src/test/resources/test-list.txt")
            .outputFilePath("./src/test/resources/test-results.csv")
            .executorService(executorService)
            .build();

    }

    @Test
    public void processAllImages() {
        imageProcessor
            .processAllImages();

    }
}