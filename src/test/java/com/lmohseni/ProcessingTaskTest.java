package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProcessingTaskTest {

    @Mock
    ConcurrentHashMap<String, String[]> map;

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    final File referenceImage = new File("src/test/resources/test-image.jpg");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        task = new ProcessingTask(imageUrl);

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
        final String[] expected = {"http://i.imgur.com/TKLs9lo.jpg", "ffffff", "fffefe", "fff7f7"};
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

    @Test(expected = java.lang.IllegalThreadStateException.class)
    public void downloadImageInvalidUrl() {
        final ProcessingTask task = new ProcessingTask("invalidUrl");
        task.downloadImage();
    }

    @Test(expected = java.lang.IllegalThreadStateException.class)
    public void downloadImageNotAJpg() {
        final ProcessingTask task = new ProcessingTask("http://google.com");
        task.getColorOccurrences(null);
    }

    private void retrieveReferenceImage() throws Exception {
        final BufferedImage image = task.downloadImage();
        ImageIO.write(image, "JPEG", referenceImage);
    }


}