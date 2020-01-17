package com.lmohseni;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProcessingTaskTest {

    @Test
    public void run() {
    }

    ProcessingTask task = new ProcessingTask("http://i.imgur.com/TKLs9lo.jpg");

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

    @Test
    public void downloadImage() throws IOException {
        final BufferedImage actual = task
            .downloadImage();
        final BufferedImage expected = ImageIO.read(referenceImage);

        assertEquals(expected.getHeight(), actual.getHeight());

    }

    public void retrieveReferenceImage() throws IOException {
        final BufferedImage image = task.downloadImage();
        ImageIO.write(image, "JPEG", referenceImage);

    }

    @Test
    public void processImage() {
        final ColorProcessingResult result = task.processImage();

        assertEquals(task.getImageUrl(), result.getImageUrl());
        assertEquals("ffffff", result.getColor1());
        assertEquals("fffefe", result.getColor2());
        assertEquals("fff7f7", result.getColor3());
    }

}