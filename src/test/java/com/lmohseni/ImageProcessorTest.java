package com.lmohseni;

import org.junit.Before;
import org.junit.Ignore;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class ImageProcessorTest {

    ImageProcessor imageProcessor;

    private String inputFile =
        "file:./src/test/resources/test-list.txt";

    private String outputFile =
        "./src/test/resources/test-results.csv";

    private URL url;

    @Mock
    Map<URL, String> cache;

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
    public void processAllImages() {
        imageProcessor
            .processAllImages();
    }


    @Test
    public void downloadImage() throws IOException {
        URL url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");
        final BufferedImage image = imageProcessor.downloadImage(url);
        final int actual = image.getHeight();
        final int expected = 632;
        assertEquals(expected, actual);
    }

    @Test
    public void processOneImage() throws IOException {
        URL url = new URL("https://i.redd.it/4m5yk8gjrtzy.jpg");
        imageProcessor.processOneImage(url, cache, true);
        verify(cache).containsKey(url);
    }

    @Test
    public void writeResult() throws IOException {
        final String testPath = "./src/test/resources/test-writer.txt";
        final BufferedWriter writer = Files
            .newBufferedWriter(Path.of(testPath));
        imageProcessor.writeResult("test message\n", writer);
        final String actual = Files.readString(Path.of(testPath));
        final String expected = "test message\n";
        assertEquals(expected, actual);

    }

    @Test
    public void formatResult() {
        final int[][] palette = {
            new int[]{1, 2, 3},
            new int[]{4, 5, 6},
            new int[]{7, 8, 9}
        };
        final String actual = imageProcessor.formatResult(url, palette);
        final String expected = "https://i.redd.it/4m5yk8gjrtzy.jpg,#123,#456,#789\n";
        assertEquals(expected, actual);
    }

    @Test
    public void convertRgbArrayToHexColor123() {
        int[] rgb = new int[]{1, 2, 3};
        final String actual = imageProcessor.convertRgbArrayToHexColor(rgb);
        final String expected = "#010203";
        assertEquals(expected, actual);
    }

    @Test
    public void convertRgbArrayToHexColorABC() {
        int[] rgb = new int[]{10, 11, 12};
        final String actual = imageProcessor.convertRgbArrayToHexColor(rgb);
        final String expected = "#0A0B0C";
        assertEquals(expected, actual);
    }


    @Test
    @Ignore
    public void testGithubUrlNoCache() {
        String github =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
        ImageProcessor.builder()
            .colorCount(3)
            .quality(1)
            .ignoreWhite(true)
            .enableCache(false)
            .inputFile(github)
            .outputFile(outputFile)
            .build()

            .processAllImages();

    }


}