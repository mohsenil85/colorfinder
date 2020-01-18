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
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Data
public class ImageProcessor {

    //tunables:
    private final int timeout;
    @NonNull
    private final TimeUnit timeUnit;
    @NonNull
    private final String imageListUrl;
    @NonNull
    private final File outputFile;
    @NonNull
    private final CompletionService<String[]> completionService;
    private final float compressionPercentage;

    public void processAllImages() {
        Instant start = Instant.now();

        final URL imagesUrl;
        try {
            imagesUrl = new URL(imageListUrl);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(imagesUrl.openStream()));
            reader
                .lines()
                .forEach(url -> {
                        System.out.println("creating thread for url: " + url);
                        completionService.submit(new ProcessingTask(url, compressionPercentage));
                    }
                );
        } catch (IOException e) {
            e.printStackTrace();
        }

        final BufferedWriter writer;
        try {
            writer = new BufferedWriter(
                new FileWriter(outputFile));
            int idx = 0;
            while (true) {
                try {
                    final Future<String[]> take = completionService.poll(timeout, timeUnit);
                    if (take == null) {
                        break;
                    }
                    final String[] strings = take.get();
                    if (strings != null) {
                        for (String str : strings) {
                            writer.write(str);
                            writer.write(",");
                        }
                        writer.newLine();

                        idx++;
                        System.out.println(idx);

                    }

                } catch (InterruptedException | ExecutionException | NullPointerException | IOException e) {
                    System.out.println(e.getMessage());

                }

                writer.flush();


                System.out.println("total records processed:  " + idx);
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).getSeconds();
                System.out.println("elapsed time:  " + timeElapsed);
            }
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).getSeconds();
            System.out.println("total time:  " + timeElapsed);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
