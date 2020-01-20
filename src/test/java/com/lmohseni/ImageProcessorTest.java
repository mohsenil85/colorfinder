package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Map<String,String[]> localCache;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        imageProcessor = ImageProcessor.builder()
            .timeout(5)
            .imageListUrl("file:./src/test/resources/test-list.txt")
            .outputFilePath("./src/test/resources/test-results.csv")
            .executorService(executorService)
            .localCache(localCache)
            .build();

    }

    @Test
    public void processAllImages() {
        imageProcessor
            .processAllImages();

    }
}