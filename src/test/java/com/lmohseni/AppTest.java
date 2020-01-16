package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppTest {

    App app = new App();

    final String referenceImageUrl = "http://i.imgur.com/TKLs9lo.jpg";

    final File referenceImage = new File("src/test/resources/test-image.jpg");

    @Before
    public void setup() {
        if (!referenceImage.exists()) {
            try {
                retrieveReferenceImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test(expected = IOException.class)
    public void downloadImageInvalid() throws IOException {
        app.downloadImage("invalidHttp");
    }

    @Test(expected = IOException.class)
    public void downloadImageNonexistant() throws IOException {
        app.downloadImage("http://foo.example");
    }

    @Test
    public void downloadImage() throws IOException {
        final BufferedImage actual = app
            .downloadImage(referenceImageUrl);
        final BufferedImage expected = ImageIO.read(referenceImage);
        assertTrue(areImagesEqual(expected, actual));
    }

    private boolean areImagesEqual(BufferedImage expected, BufferedImage actual) {
        //quick and dirty way to compare images, relies on java serialization
        final int ignoredLength = 22;  //ignore the literal class name
        return
            expected.toString().substring(ignoredLength)
                .equals(actual.toString().substring(ignoredLength));
    }

    private void retrieveReferenceImage() throws IOException {
        final BufferedImage image = app
            .downloadImage(referenceImageUrl);
        ImageIO.write(image, "JPEG", referenceImage);

    }

    @Test
    public void processImage() {
        final App.ColorProcessingResult result = app.processImage(referenceImageUrl);

        assertEquals(referenceImageUrl,result.getImageUrl());
        assertEquals("ffffff",result.getColor1());
        assertEquals("fffefe",result.getColor2());
        assertEquals("fff7f7",result.getColor3());
    }

}
