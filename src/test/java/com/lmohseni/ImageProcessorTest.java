package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.verify;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Map<String, StringBuilder> cache;

    @Mock
    private Set<String> dropList;

    private String inputFile =
        "file:./src/test/resources/test-list.txt";

    private String outputFile =
        "./src/test/resources/test-results.csv";

    @Before
    public void setUp() throws MalformedURLException {

        MockitoAnnotations.initMocks(this);

        imageProcessor = ImageProcessor.builder()
            .timeout(1)
            .inputFile(inputFile)
            .outputFile(outputFile)
            .executorService(executorService)
            .cache(cache)
            .dropList(dropList)
            .build();

    }


    @Test
    public void processAllImages() throws IOException {
        imageProcessor
            .processAllImages();

        verify(executorService).shutdownNow();
    }

}