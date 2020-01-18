package com.lmohseni;

import lombok.Data;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Data
public class ImageProcessor {

    //tunables:
    private final int nThreads;
    private final int timeout;
    private final int initialCapacity;
    private final float loadFactor;
    private final int concurrencyLevel;
    @NonNull
    private final TimeUnit timeUnit;
    @NonNull
    private final String imageListUrl;
    @NonNull
    private final File outputFile;

    private ThreadPoolExecutor executor;
    private CompletionService<String[]> completionService;
    private ConcurrentHashMap<Integer, String[]> resultsMap;

    public void init() {
//        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        resultsMap = new ConcurrentHashMap<Integer, String[]>(initialCapacity, loadFactor,
            concurrencyLevel);
        completionService = new ExecutorCompletionService<String[]>(executor);
    }

    public ConcurrentHashMap<Integer, String[]> processAllImages() throws IOException {
        try (BufferedReader read = new BufferedReader(
            new InputStreamReader(new URL(imageListUrl).openStream()))) {

            try (Stream<String> lines = read.lines()) {
                lines.forEach(url ->
                    completionService.submit(new ProcessingTask(url))
                );
            }
        }

        int idx = 0;
        while (true) {
            try {
                final Future<String[]> take = completionService.poll(timeout, timeUnit);
                if (take == null) {
                    break;
                }
                final String[] strings = take.get();
                resultsMap.put(idx, strings);
                idx++;
                System.out.println(idx);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdownNow();

        return resultsMap;
    }

    public int writeOutputFile(ConcurrentHashMap<Integer, String[]> results) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for (String[] array : results.values()) {
                for (String str : array) {
                    writer.write(str);
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.close();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


}
