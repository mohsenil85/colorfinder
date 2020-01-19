package com.lmohseni.benchmark;

import com.lmohseni.ImageProcessor;
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

@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class foo {

    public static void main(String[] args) throws Exception {

        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void testImageProcessorForkJoin() {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

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
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    public void testImageProcessorForkJoin25() {
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
    public void testImageProcessorForkJoin200() {
        final String defaultInputUrl = "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";

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
            defaultInputUrl,
            outputFile,
            completionService,
            compressionPercentage
        );
        imageProcessor.processAllImages();

    }

    @Benchmark
    public void testImageProcessorForkJoin400() {
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

}
