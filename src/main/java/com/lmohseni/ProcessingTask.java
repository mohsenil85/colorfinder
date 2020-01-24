package com.lmohseni;

import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.concurrent.ReentrantReadWriteLock;
import de.androidpit.colorthief.ColorThief;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Data
@Builder
public class ProcessingTask implements SuspendableRunnable {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;

    @NonNull
    private final ConcurrentHashMap<String, String> cache;
    @NonNull
    private final Set<String> dropList;

    @NonNull
    private final String outputFile;


    @NonNull
    private final CountDownLatch latch;

    @NonNull
    private final BufferedWriter writer;


    @SneakyThrows
    @Override
    public void run() {

        boolean locked = false;

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        final String name = Strand.currentStrand().getName();

//        if (dropList.contains(imageUrl)) {
//            System.out.printf("%s: ignoring %s%n", name, imageUrl);
//            latch.countDown();
//            return;
//        }

        final String cached;
        locked = readLock.tryLock(500, TimeUnit.MILLISECONDS);
        if (locked) {
            try {
                cached = cache.get(imageUrl);
                if (cached != null) {
                    System.out.printf("%s: cache hit %s", name, cached);
                    writer.write(cached);
                    latch.countDown();
                    return;
                }
            } finally {
                readLock.unlock();
            }
        }
        final BufferedImage image = downloadImage();

        if (null == image) {
            System.out.printf("%s, problem with: %s%n", name, imageUrl);
            latch.countDown();
            return;
        }

        final String result = constructResult(image);

        locked = writeLock.tryLock(10, TimeUnit.MILLISECONDS);
        if (locked) {
            try {
                cache.putIfAbsent(imageUrl, result);
            } finally {
                writeLock.unlock();
            }
        }

        System.out.printf("%s recorded %s", name, result);

        writer.write(result);

        latch.countDown();


    }

    private String constructResult(BufferedImage image) {
        final StringBuilder result = new StringBuilder();
        final int[][] palette = ColorThief.getPalette(
            image,
            colorCount,
            quality,
            ignoreWhite
        );

        result.append(imageUrl);

        for (int i = 0; i < colorCount; i++) {
            result.append(",");
            result.append(convertRgbArrayToHexColor(palette[i]));
        }
        result.append("\n");
        return result.toString();
    }

    String convertRgbArrayToHexColor(int[] rgb) {

        return String.format(
            "#%s%s%s",
            Integer.toHexString(rgb[0]),
            Integer.toHexString(rgb[1]),
            Integer.toHexString(rgb[2])
        ).toUpperCase();
    }

    BufferedImage downloadImage() {

        try {

            final URL url = new URL(imageUrl);
            final InputStream inputStream = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(
                new BufferedInputStream(inputStream));
            return bufferedImage;

        } catch (FileNotFoundException e) {
            System.out.printf("adding %s to ignore list%n", imageUrl);
            dropList.add(imageUrl);
        } catch (MalformedURLException e) {
            System.out.printf("malformed url: %s%n", imageUrl);
            dropList.add(imageUrl);
        } catch (IOException e) {
            System.out.printf("error : %s%n", e.getMessage());
        }
        return null;
    }

}
