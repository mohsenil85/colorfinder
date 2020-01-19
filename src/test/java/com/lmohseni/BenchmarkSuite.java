package com.lmohseni;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx8G"}, warmups = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class BenchmarkSuite {

    @Benchmark
    @Test
    public void testProcessingTask50pct() {

        final String[] actual = ProcessingTask.builder()
            .imageUrl("https://i.redd.it/ftd3sx5ah13z.jpg")
            .verbose(true)
            .compressionPercentage(.5f)
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
    public void testProcessingTask10pct() {

        final String[] actual = ProcessingTask.builder()
            .imageUrl("https://i.redd.it/ftd3sx5ah13z.jpg")
            .verbose(true)
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
    public void testImageProcessorForkJoin() {
        ImageProcessor.builder()
            .verbose(true)
            .compressionPercentage(.5f)
            .timeout(5)
            .timeUnit(TimeUnit.SECONDS)
            .imageListUrl("file:./src/test/resources/test-list.txt")
            .outputFilePath("./target/results.csv")
            .executorService(Executors.newWorkStealingPool(25))
            .build()

            .processAllImages();
    }
}
