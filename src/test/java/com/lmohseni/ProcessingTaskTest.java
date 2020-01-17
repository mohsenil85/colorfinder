package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessingTaskTest {

    @Mock
    ConcurrentHashMap<String, String[]> map;

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    final File referenceImage = new File("src/test/resources/test-image.jpg");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        task = new ProcessingTask(
            imageUrl,
            map
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
    public void run() {
        when(map.put(anyString(), any(String[].class))).thenReturn(null);
        task.run();
        verify(map).put(imageUrl, new String[]{"ffffff", "fffefe", "fff7f7"});
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
        new ProcessingTask(null, map);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void downloadImageNullMap() {
        new ProcessingTask(imageUrl, null);
    }


    @Test(expected = java.lang.IllegalThreadStateException.class)
    public void downloadImageInvalidUrl() {
        final ProcessingTask task = new ProcessingTask("invalidUrl", map);
        task.downloadImage();
    }

    @Test(expected = java.lang.IllegalThreadStateException.class)
    public void downloadImageNotAJpg() {
        final ProcessingTask task = new ProcessingTask("http://google.com", map);
        task.getColorOccurrences(null);
    }

    private void retrieveReferenceImage() throws Exception {
        final BufferedImage image = task.downloadImage();
        ImageIO.write(image, "JPEG", referenceImage);
    }


}