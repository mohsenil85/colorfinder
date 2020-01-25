package com.lmohseni;

import co.paralleluniverse.fibers.SuspendExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@Ignore
public class ProcessingTaskTest {

    final String imageUrl = "http://i.imgur.com/TKLs9lo.jpg";
    ProcessingTask task;

    File referenceImage;

    @Mock
    Map<String, String> cache;

    @Mock
    Set<String> dropList;

    @Mock
    CountDownLatch latch;

    BufferedWriter writer;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        final String outputFileName = "./target/test-output.csv";

        writer = new BufferedWriter(
            new FileWriter(outputFileName)
        );

        task = ProcessingTask.builder()
            .imageUrl(imageUrl)
            .colorCount(3)
            .quality(5)
            .ignoreWhite(false)
            .cache(cache)
            .outputFile(outputFileName)
            .dropList(dropList)
            .latch(latch)
            .writer(writer)
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
    public void run() throws InterruptedException, SuspendExecution {
        task.run();
        assertFalse(verify(dropList).contains(anyString()));
    }

//    @Test
//    public void downloadImage() throws Exception {
//        final BufferedImage actual = task
//            .downloadImage(task.getImageUrl());
//        final BufferedImage expected = ImageIO.read(referenceImage);
//
//        assertEquals(expected.getHeight(), actual.getHeight());
//
//    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void downloadImageNullUrl() {
        ProcessingTask.builder()
            .imageUrl(null)
            .build();
    }

//    @Test
//    public void downloadImageInvalidUrl() {
//        ProcessingTask.builder()
//            .imageUrl("invalidUrl")
//            .cache(cache)
//            .outputFile("./target/test-output.csv")
//            .dropList(dropList)
//            .latch(latch)
//            .writer(writer)
//            .scheduler(scheduler)
//            .build()
//            .downloadImage(task.getImageUrl());
//    }

//    @Test
//    public void downloadImageTroublesomeUrl() {
//        ProcessingTask.builder()
//            .imageUrl("https://i.redd.it/nrafqoujmety.jpg")
//            .cache(cache)
//            .outputFile("./target/test-output.csv")
//            .dropList(dropList)
//            .latch(latch)
//            .writer(writer)
//            .scheduler(scheduler)
//            .build()
//            .downloadImage(task.getImageUrl());
//    }

//    @Test
//    public void getColorOccurrences() {
//        ProcessingTask.builder()
//            .imageUrl("https://i.redd.it/m4cfqp8wfv5z.jpg")
//            .cache(cache)
//            .outputFile("./target/test-output.csv")
//            .dropList(dropList)
//            .latch(latch)
//            .writer(writer)
//            .scheduler(scheduler)
//            .build()
//            .downloadImage(task.getImageUrl());
//    }


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
//        final BufferedImage image = task.downloadImage(imageUrl);
//        ImageIO.write(image, "JPEG", referenceImage);
    }


}