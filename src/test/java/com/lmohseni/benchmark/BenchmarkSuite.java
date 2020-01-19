package com.lmohseni.benchmark;

import com.lmohseni.ImageProcessor;
import com.lmohseni.ProcessingTask;
import org.junit.Ignore;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.io.File;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx8G"}, warmups = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class BenchmarkSuite {

    @Benchmark
    public void testProcessingTask50pct() {

        final String testImageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
        final float compressionPercentage = .5f;
        final ProcessingTask task = new ProcessingTask(testImageUrl, compressionPercentage);
        final String[] actual = task.call();
        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };
    }


    @Benchmark
    @Test
    public void testProcessingTask10pct() {

        final String testImageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
        final float compressionPercentage = .1f;
        final ProcessingTask task = new ProcessingTask(testImageUrl, compressionPercentage);
        final String[] actual = task.call();
        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };
    }

    @Benchmark
    @Test
    public void testProcessingTask90pct() {

        final String testImageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
        final float compressionPercentage = .9f;
        final ProcessingTask task = new ProcessingTask(testImageUrl, compressionPercentage);
        final String[] actual = task.call();
        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoin25() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";

        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool(25);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }


    @Benchmark
    @Test
    @Ignore("Takes a long time")
    public void testImageProcessorForkJoin25FullList() {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool(25);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    @Test
    @Ignore("Don't run live test on CI")
    public void testImageProcessorForkJoin100FullList() {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            Executors.newWorkStealingPool(100));

        new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        ).processAllImages();

    }


    @Benchmark
    @Test
    @Ignore("Don't run live test on CI")
    public void testImageProcessorForkJoin400FullList() {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool(400);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoin() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool();

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoin200() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newWorkStealingPool(200);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    @Test
    public void testImageProcessorCached() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newCachedThreadPool();

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    @Test
    public void testImageProcessorFixed500() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newFixedThreadPool(500);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }


    @Benchmark
    @Test
    public void testImageProcessorFixed200() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newFixedThreadPool(200);

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }


    @Benchmark
    @Test
    public void testImageProcessorSingleThread() {
        final String localTestFilePath = "file:./src/test/resources/test-list.txt";
        ImageProcessor imageProcessor;
        File outputFile = new File("./target/results.csv");

        int timeout = 5;
        float compressionPercentage = .9f;
        final ExecutorService executor = Executors
            .newSingleThreadExecutor();

        final ExecutorCompletionService<String[]> completionService = new ExecutorCompletionService<>(
            executor);

        imageProcessor = new ImageProcessor(
            timeout,
            TimeUnit.SECONDS,
            localTestFilePath,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }


}

class Runner {

    public static void main(String[] args) throws Exception {

        org.openjdk.jmh.Main.main(args);
    }
}

