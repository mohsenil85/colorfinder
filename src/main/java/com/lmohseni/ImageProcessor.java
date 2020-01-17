package com.lmohseni;

import lombok.Data;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
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
    private final int initialCapacity;
    private final int loadFactor;
    @NonNull
    private final String imageListUrl;

    private ThreadPoolExecutor executor;
    private ConcurrentHashMap<String, String[]> resultsMap;

    public void init(){
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        resultsMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, nThreads);
    }


    public ConcurrentHashMap<String, String[]> processAllImages() throws IOException {
        Set<String> urls;
        try (BufferedReader read = new BufferedReader(
            new InputStreamReader(new URL(imageListUrl).openStream()))) {

            //each thread will be reading from this
            urls = Collections.synchronizedSet(new HashSet<String>());

            try (Stream<String> lines = read.lines()) {
                lines.forEach(line -> urls.add(line));
            }
        }

        for (String url : urls) {
            executor.execute(
                new ProcessingTask(url, resultsMap)
            );
        }

        executor.shutdown();
        try {
            final boolean b = executor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultsMap;

    }

}
