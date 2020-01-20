package com.lmohseni;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProcessingTaskTest {

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    File referenceImage;

    @Mock
    Map<String, String[]> map;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        task = new ProcessingTask(imageUrl, 3, 5, true, map);

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
            "#D9D8D6",
            "#3D4557",
            "#C33941"
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
        final BufferedImage image = ProcessingTask.builder()
            .imageUrl(null)
            .build()
            .downloadImage();
    }

    @Test
    public void downloadImageInvalidUrl() {
        final BufferedImage image = ProcessingTask.builder()
            .imageUrl("invalidUrl")
            .build()
            .downloadImage();
    }

    @Test
    public void getColorOccurrences() {
        final BufferedImage image = ProcessingTask.builder()
            .imageUrl("https://i.redd.it/m4cfqp8wfv5z.jpg")
            .build()
            .downloadImage();
    }


    @Test
    public void convertRgbArrayToHexColor123() {
        final String actual = task.convertRgbArrayToHexColor(new int[]{1, 2, 3});
        final String expected = "#123";
        assertEquals(expected, actual);
    }

    @Test
    public void convertRgbArrayToHexColorABC() {
        final String actual = task.convertRgbArrayToHexColor(new int[]{10, 11, 12});
        final String expected = "#ABC";
        assertEquals(expected, actual);
    }


    private void retrieveReferenceImage() throws Exception {
        final BufferedImage image = task.downloadImage();
        ImageIO.write(image, "JPEG", referenceImage);
    }


}