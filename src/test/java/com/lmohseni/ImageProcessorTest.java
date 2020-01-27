package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    private String inputFile =
        "file:./src/test/resources/test-list.txt";

    private String outputFile =
        "./src/test/resources/test-results.csv";

    private URL url;

    @Mock
    Map<URL, String> cache;

    @Mock
    Set<URL> dropList;


    @Before
    public void setUp() throws MalformedURLException {

        url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");

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


    @Test
    public void downloadImage() throws IOException {
        URL url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");
        imageProcessor.downloadImage(url, dropList);
    }


    @Test
    public void processOneImage() throws IOException {
        URL url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");
        imageProcessor.processOneImage(url, cache, dropList, true);
    }

    @Test
    public void writeResult() throws IOException {
        final BufferedWriter writer = Files
            .newBufferedWriter(Path.of("./src/test/resources/test-writer.txt"));
        imageProcessor.writeResult("test message\n", writer);
    }

    @Test
    public void formatResult() throws IOException {
        final BufferedWriter writer = Files
            .newBufferedWriter(Path.of("./src/test/resources/test-writer.txt"));
//        imageProcessor.formatResult(url, new int);
    }


    @Test
    public void testGithubUrlNoCache() throws IOException {
        String github =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
        ImageProcessor.builder()
            .colorCount(3)
            .quality(10)
            .ignoreWhite(true)
            .enableCache(false)
            .inputFile(github)
            .outputFile(outputFile)
            .build()

            .processAllImages();

    }


}