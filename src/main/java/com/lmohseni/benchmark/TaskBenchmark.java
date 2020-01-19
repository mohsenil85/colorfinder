package com.lmohseni.benchmark;

import com.lmohseni.ProcessingTask;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;


@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class TaskBenchmark {

    public static void main(String[] args) throws Exception {

        org.openjdk.jmh.Main.main(args);
    }

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


}
