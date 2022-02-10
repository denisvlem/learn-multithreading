package com.denisvlem.synchronization.barriers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Demo class represents plain {@link CountDownLatch} use case.
 * There are two groups of workers: builder and painters.
 * Builder construct furniture and painters paint it.
 * Since painting requires special respiratory equipment painters can not start their work until builders are done.
 */
public class CountDownLatchDemo {

    public static void main(String[] args) {
        var object = List.of("Table", "Closet", "Chair", "Bed");
        final CountDownLatch latch = new CountDownLatch(object.size());
        ExecutorService exec = Executors.newCachedThreadPool();


        for (String s : object) {
            //Each painter and builder can work independently within a group
            //We can deploy them as separated threads
            //But all the painters must wait until the builders finished
            exec.submit(new Builder(latch, s));
            exec.submit(new Painter(latch, s));
        }

        exec.shutdown();
    }

    public record Builder(CountDownLatch latch, String object) implements Runnable {

        @Override
        public void run() {
            System.out.println("Building " + object + "...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //After each builder is done the counter is decremented
            //When it reaches zero painters are free to start
            latch.countDown();
        }
    }

    public record Painter(CountDownLatch latch, String paintObject) implements Runnable {

        @Override
        public void run() {
            try {
                //Painters wait until all the builders finish their work
                latch.await();
                System.out.println("Painting " + paintObject + "...");
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
