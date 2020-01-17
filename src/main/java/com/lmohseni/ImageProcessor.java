package com.lmohseni;

import lombok.Data;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletionService;
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
    @NonNull
    private final TimeUnit timeUnit;
    @NonNull
    private final String imageListUrl;

    private ThreadPoolExecutor executor;
    private HashMap<Integer, String[]> resultsMap;
    private CompletionService<String[]> completionService;

    public void init() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        resultsMap = new HashMap<Integer, String[]>();
        completionService = new ExecutorCompletionService<String[]>(executor);
    }

    public HashMap<Integer, String[]> processAllImages() throws IOException {
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
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdownNow();

        return resultsMap;

    }

}
