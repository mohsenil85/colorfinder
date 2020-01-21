package com.lmohseni;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Fork(value = 2, jvmArgs = {"-Xms8G", "-Xmx12G"}, warmups = 2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.All)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
public class BenchmarkTest {

    @State(org.openjdk.jmh.annotations.Scope.Thread)
    static class Scope {

        static String imageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
        static String localUrl = "file:./src/test/resources/test-list.txt";
        static String github =
            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
        static String testOutput = "./target/results.csv";
        static int timeout = 3;
        static int nThreads = 10;
        static int colorCount = 3;
        static int quality = 50;
        static boolean ignoreWhite = false;
        static Map<String,String[]> cache = new ConcurrentHashMap<>();
        static Set<String> dropList = ConcurrentHashMap.newKeySet();

    }

    public static class BenchmarkRunner {

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(args);
        }
    }

    @Benchmark
    @Test
    public void BenchProcessingTasks() {
        ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .cache(Scope.cache)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .dropList(Scope.dropList)
            .ignoreWhite(Scope.ignoreWhite)
            .build()

            .call();
    }

    @Benchmark
    @Test
    public void testProcessingTask10() {
        ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .cache(Scope.cache)
            .colorCount(Scope.colorCount)
            .quality(10)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .build()

            .call();
    }

    @Benchmark
    @Test
    public void testProcessingTask30() {
        ProcessingTask.builder()
            .imageUrl(Scope.imageUrl)
            .cache(Scope.cache)
            .colorCount(Scope.colorCount)
            .quality(30)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .build()

            .call();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinLocal() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.localUrl)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedLocal() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.localUrl)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorSingleThreadedLocal() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.localUrl)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newSingleThreadExecutor())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorCachedLocal() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.localUrl)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newCachedThreadPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote25() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool(25))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote200() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .cache(Scope.cache)
            .executorService(Executors.newWorkStealingPool(200))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorForkJoinRemote1000() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newWorkStealingPool(1000))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote50() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote200() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(200))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorFixedRemote100() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newFixedThreadPool(1000))
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorCachedRemote() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newCachedThreadPool())
            .build()

            .processAllImages();
    }

    @Benchmark
    @Test
    public void testImageProcessorSingleThreadedRemote() {
        ImageProcessor.builder()
            .timeout(Scope.timeout)
            .colorCount(Scope.colorCount)
            .quality(Scope.quality)
            .ignoreWhite(Scope.ignoreWhite)
            .dropList(Scope.dropList)
            .inputFile(Scope.github)
            .cache(Scope.cache)
            .outputFile(Scope.testOutput)
            .executorService(Executors.newSingleThreadExecutor())
            .build()

            .processAllImages();
    }


}

