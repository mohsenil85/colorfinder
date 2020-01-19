package com.lmohseni;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx8G"}, warmups = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.All)
@Warmup(iterations = 2)
@Measurement(iterations = 10)
@Ignore("Don't run in CI")
public class BenchmarkTest {

    @State(org.openjdk.jmh.annotations.Scope.Thread)
    static class Scope {

        static String imageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
        static String localUrl = "file:./src/test/resources/test-list.txt";
        static String github =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
        static String testOutput = "./target/results.csv";
        static boolean verbose = false;
        static float compression = .5f;
        static int timeout = 5;
        static int nThreads = 50;

    }

    static class BenchmarkRunner {

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(args);
        }
    }

    @Benchmark
    @Test
    public void BenchProcessingTasks() {

        final String[] actual = ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .build()

            .call();

        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };

        Assert.assertArrayEquals(expected, actual);
    }

    @Benchmark
    @Test
    public void testProcessingTask10() {

        final String[] actual = ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .build()

            .call();

        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };

        Assert.assertArrayEquals(expected, actual);
    }

    @Benchmark
    @Test
    public void testProcessingTask90() {

        final String[] actual = ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .verbose(Scope.verbose)
            .compressionPercentage(.9f)
            .build()

            .call();

        final String[] expected = new String[]{
            "https://i.redd.it/ftd3sx5ah13z.jpg",
            "#FFB44B",
            "#FFFDFE",
            "#FFB54B"
        };

        Assert.assertArrayEquals(expected, actual);
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinLocal() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.localUrl)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedLocal() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.localUrl)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorSingleThreadedLocal() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.localUrl)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newSingleThreadExecutor())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorCachedLocal() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.localUrl)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newCachedThreadPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote25() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool(25))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote200() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool(200))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote1000() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool(1000))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote50() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote200() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(200))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote100() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(Scope.compression)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(1000))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorCachedRemote() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newCachedThreadPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorSingleThreadedRemote() {
        ImageProcessor.builder()
            .verbose(Scope.verbose)
            .compressionPercentage(.1f)
            .timeout(Scope.timeout)
            .imageListUrl(Scope.github)
            .outputFilePath(Scope.testOutput)
            .executorService(Executors.newSingleThreadExecutor())
            .build()

            .processAllImages();
    }


}

