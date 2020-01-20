package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProcessingTaskTest {

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    File referenceImage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        task = new ProcessingTask(imageUrl);

        referenceImage = new File(
            Objects.requireNonNull(getClass().getClassLoader().getResource("test-image.jpg"))
                .getFile()
        );

        if (!referenceImage.exists()) {
            try {
                retrieveReferenceImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void call() {
        final String[] expected = {
            "http://i.imgur.com/TKLs9lo.jpg",
            "#F1F0F0",
            "#3D4658",
            "#D62C35"
        };
        final String[] actual = task.call();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void downloadImage() throws Exception {
        final BufferedImage actual = task
            .downloadImage();
        final BufferedImage expected = ImageIO.read(referenceImage);

        assertEquals(expected.getHeight(), actual.getHeight());

    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void downloadImageNullUrl() {
        new ProcessingTask(null);
    }

    @Test
    public void downloadImageInvalidUrl() {
        final ProcessingTask task = new ProcessingTask("invalidUrl");
        task.downloadImage();
    }

    @Test
    public void getColorOccurrences() {
        final BufferedImage image = ProcessingTask.builder()
            .imageUrl("https://i.redd.it/m4cfqp8wfv5z.jpg")
            .build()
            .downloadImage();
    }

    private void retrieveReferenceImage() throws Exception {
        final BufferedImage image = task.downloadImage();
        ImageIO.write(image, "JPEG", referenceImage);
    }


}