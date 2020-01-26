package com.lmohseni;

import co.paralleluniverse.common.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;

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

        url =  new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");

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
    public void detectPalette() throws IOException {
        BufferedImage image = mock(BufferedImage.class);
        URL url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");
        Pair<URL, BufferedImage> data = new Pair<>(url, image);
        imageProcessor
            .detectPalette(data, cache);
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

//    @Test
//    public void formatResult() throws IOException {
//        final BufferedWriter writer = Files
//            .newBufferedWriter(Path.of("./src/test/resources/test-writer.txt"));
//        imageProcessor.formatResult(url, new int);
//    }


}