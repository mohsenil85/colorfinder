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
public class ImageProcessorBenchmark {

    public static void main(String[] args) throws Exception {

        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
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
