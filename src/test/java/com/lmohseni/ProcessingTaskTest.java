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
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProcessingTaskTest {

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    File referenceImage;

    @Mock
    Map<String, StringBuilder> cache;

    @Mock
    Set<String> dropList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        task = ProcessingTask.builder()
            .imageUrl(imageUrl)
            .colorCount(3)
            .quality(5)
            .ignoreWhite(false)
            .cache(cache)
            .dropList(dropList)
            .build();

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
    public void call() throws InterruptedException {
//        final String[] expected = {
//            "http://i.imgur.com/TKLs9lo.jpg",
//            "#F1F0F0",
//            "#3F4758",
//            "#D62C35"
//        };
//        final String[] actual =
        task.run();
//        assertArrayEquals(expected, actual);
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
        ProcessingTask.builder()
            .imageUrl(null)
            .build()
            .downloadImage();
    }

    @Test
    public void downloadImageInvalidUrl() {
        ProcessingTask.builder()
            .imageUrl("invalidUrl")
            .cache(cache)
            .dropList(dropList)
            .build()
            .downloadImage();
    }

    @Test
    public void downloadImageTroublesomeUrl() {
        ProcessingTask.builder()
            .imageUrl("https://i.redd.it/nrafqoujmety.jpg")
            .cache(cache)
            .dropList(dropList)
            .build()
            .downloadImage();
    }


    @Test
    public void getColorOccurrences() {
        ProcessingTask.builder()
            .imageUrl("https://i.redd.it/m4cfqp8wfv5z.jpg")
            .cache(cache)
            .dropList(dropList)
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