//package com.lmohseni;
//
//import co.paralleluniverse.fibers.SuspendExecution;
//import org.junit.jupiter.api.Test;
//import org.openjdk.jmh.annotations.Benchmark;
//import org.openjdk.jmh.annotations.BenchmarkMode;
//import org.openjdk.jmh.annotations.Fork;
//import org.openjdk.jmh.annotations.Measurement;
//import org.openjdk.jmh.annotations.Mode;
//import org.openjdk.jmh.annotations.OutputTimeUnit;
//import org.openjdk.jmh.annotations.State;
//import org.openjdk.jmh.annotations.Warmup;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//@Fork(value = 1, jvmArgs = {"-Xms8G", "-Xmx12G",
//    "-javaagent: /Users/lmohseni/.m2/repository/co/paralleluniverse/quasar-core/0.8.0/quasar-core-0.8.0.jar"}, warmups = 1)
//@OutputTimeUnit(TimeUnit.SECONDS)
//@BenchmarkMode(Mode.All)
//@Warmup(iterations = 1)
//@Measurement(iterations = 3)
//public class BenchmarkTest {
//
//    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
//    static class Scope {
//
//        static String imageUrl = "https://i.redd.it/ftd3sx5ah13z.jpg";
//        static String localUrl = "file:./src/test/resources/test-list.txt";
//        static String REALLY__LONG__ = "file:./src/test/resources/test-list2.txt";
//        static String github =
//            "https://gist.githubusercontent.com/ehmo/e736c827ca73d84581d812b3a27bb132/raw/77680b283d7db4e7447dbf8903731bb63bf43258/input.txt";
//        static String testOutput = "./target/test-results.csv";
//        static int timeout = 5;
//        static int nThreads = 10;
//        static int colorCount = 3;
//        static int quality = 50;
//        static boolean ignoreWhite = false;
//        static CountDownLatch latch = new CountDownLatch(10);
//        static Set<String> dropList = ConcurrentHashMap.newKeySet();
//
//        static BufferedWriter writer;
//
//        static {
//            try {
//                writer = new BufferedWriter(
//                            new FileWriter("./target/test-results.csv"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
////    public static class BenchmarkRunner {
////
////        public static void main(String[] args) throws Exception {
////            org.openjdk.jmh.Main.main(args);
////        }
////    }
//
//    @Benchmark
//    @Test
//    public void BenchProcessingTasks() throws InterruptedException, SuspendExecution {
//        ProcessingTask.builder()
//            .imageUrl(Scope.imageUrl)
//            .cache(new ConcurrentHashMap<>())
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .dropList(Scope.dropList)
//            .outputFile(Scope.testOutput)
//            .ignoreWhite(Scope.ignoreWhite)
//            .latch(Scope.latch)
//            .build()
//
//            .run();
//    }
//
//    @Benchmark
//    @Test
//    public void testProcessingTask10() throws InterruptedException, SuspendExecution {
//        ProcessingTask.builder()
//            .imageUrl(Scope.imageUrl)
//            .cache(new ConcurrentHashMap<>())
//            .colorCount(Scope.colorCount)
//            .quality(10)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .outputFile(Scope.testOutput)
//            .build()
//
//            .run();
//    }
//
//    @Benchmark
//    @Test
//    public void testProcessingTask30() throws InterruptedException, SuspendExecution {
//        ProcessingTask.builder()
//            .imageUrl(Scope.imageUrl)
//            .cache(new ConcurrentHashMap<>())
//            .colorCount(Scope.colorCount)
//            .quality(30)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .outputFile(Scope.testOutput)
//            .build()
//
//            .run();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorForkJoinLocal() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.localUrl)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool())
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorFixedLocal() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.localUrl)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorFixedLocalLongList() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.localUrl)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorForkJoinLocalLongList() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.localUrl)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool())
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorWorkStealingLocalLong() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.REALLY__LONG__)
//            .cache(new ConcurrentHashMap<String,String>(1000,.9f,100))
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool(100))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorCachedLocal() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.localUrl)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newCachedThreadPool())
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorForkJoinRemote() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(ConcurrentHashMap.newKeySet())
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool())
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorForkJoinRemote25() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool(25))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorForkJoinRemote5() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newWorkStealingPool(5))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorFixedRuntimeAvailableProcessors() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(
//                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorFixedRemote3() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newFixedThreadPool(3))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorFixedRemote50() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newFixedThreadPool(Scope.nThreads))
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorCachedRemote() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newCachedThreadPool())
//            .build()
//
//            .processAllImages();
//    }
//
//    @Benchmark
//    @Test
//    public void testImageProcessorSingleThreadedRemote() {
//        ImageProcessor.builder()
//            .timeout(Scope.timeout)
//            .colorCount(Scope.colorCount)
//            .quality(Scope.quality)
//            .ignoreWhite(Scope.ignoreWhite)
//            .dropList(Scope.dropList)
//            .inputFile(Scope.github)
//            .cache(new ConcurrentHashMap<>())
//            .outputFile(Scope.testOutput)
//            .executorService(Executors.newSingleThreadExecutor())
//            .build()
//
//            .processAllImages();
//    }
//
//
//}
//
