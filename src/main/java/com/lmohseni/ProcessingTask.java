package com.lmohseni;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableRunnable;
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
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Data
@Builder
public class ProcessingTask implements SuspendableRunnable {

    @NonNull
    private final String imageUrl;
    private final int colorCount;
    private final int quality;
    private final boolean ignoreWhite;
    private final int size;

    @NonNull
    private final Map<String, String> cache;
    @NonNull
    private final Set<String> dropList;

    @NonNull
    private final String outputFile;

    @NonNull
    private final CountDownLatch latch;

    @NonNull
    private final BufferedWriter writer;


//    @NonNull
//    private final ExecutorService executorService;


    @NonNull
    private final FiberScheduler scheduler;

    private BufferedImage image;

    @SneakyThrows
    @Override
    public void run() {

        long idx = (long) size - latch.getCount();
        boolean locked = false;

//        final int i = ai.getAndIncrement();
        Path path = Paths.get(outputFile);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
            path, WRITE, CREATE);

//        final String name = Strand.currentStrand().getName();

        final String name = String.valueOf(idx);
        if (dropList.contains(imageUrl)) {
            System.out.printf("%s: ignoring %s%n", name, imageUrl);
            latch.countDown();
            return;
        }

        final String cached;
        cached = cache.get(imageUrl);
        if (cached != null) {
            System.out
                .printf("### %s   %s: cache hit %s", (long) size - latch.getCount(), name, cached);
            writer.write(cached);
//            writer.flush();
            latch.countDown();
            return;
        }
        final BufferedImage image = downloadImage(imageUrl, scheduler);

        if (null == image) {
            System.out.printf("%s, problem with: %s%n", name, imageUrl);
            latch.countDown();
            return;
        }

        final String result = constructResult(image);

        cache.putIfAbsent(imageUrl, result);
        writer.write(result);

//        if (latch.getCount() % 80 == 0) {
//            writer.flush();
//
//        }

        System.out.printf("l# %s * %s recorded %s", (long) size - latch.getCount(), name, result);

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

    BufferedImage downloadImage(String imageUrl, FiberScheduler scheduler) {

        try {

            final URL url = new URL(imageUrl);
            final InputStream inputStream = url.openStream();
            return ImageIO.read(
                new BufferedInputStream(inputStream));

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
