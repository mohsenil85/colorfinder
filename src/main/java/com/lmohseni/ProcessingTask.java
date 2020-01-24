package com.lmohseni;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableCallable;
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

    int scopeVar;

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
    @Suspendable
    public void run() {

        long idx = (long) size - latch.getCount();

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

//        String s = new Fiber<>((SuspendableCallable<String>) () -> {
//            System.out.printf("%s, inside!%n, size: %s%n", Strand.currentStrand().getName(), size);
//            System.out.printf("int == %s%n", scopeVar++);
////            Fiber.sleep(2000);
//            return "foo";
//        }).start().get();

//        System.out.printf("%s, %n ", s);

        final BufferedImage image = downloadImage(
            imageUrl,
            scheduler, dropList, latch);

//        if (null == image) {
//            System.out.printf("%s, problem with: %s%n", name, imageUrl);
//            latch.countDown();
//            return;
//        }

        final String result = constructResult(image);

        cache.put(imageUrl, result);
        writer.write(result);

//        if (latch.getCount() % 80 == 0) {
//            writer.flush();
//
//        }

        System.out.printf("l# %s * %s recorded %s", (long) size - latch.getCount(), name, result);

        latch.countDown();

    }

//    Fiber<int[][]> getPalette

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


    @Suspendable
    BufferedImage downloadImage(
        String url,
        FiberScheduler s,
        Set<String> dropList,
        CountDownLatch latch
    ) {

        try {
            return new Fiber<>( (SuspendableCallable<BufferedImage>) () -> {
                try {
                    return tryDownloadImage(url);

                } catch (MalformedURLException | FileNotFoundException e) {
                    System.out.printf("adding %s to droplist%n", url);
                    dropList.add(url);
                    latch.countDown();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                return null;
            }).start().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    BufferedImage tryDownloadImage(String url) throws IOException {
        final InputStream inputStream = new URL(url).openStream();
        return ImageIO.read(
            new BufferedInputStream(inputStream));

    }
}
