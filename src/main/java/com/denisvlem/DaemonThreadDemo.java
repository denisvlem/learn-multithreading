package com.denisvlem;

import java.util.concurrent.TimeUnit;

public class DaemonThreadDemo {

    private static volatile boolean isWorking = true;

    public static void main(String[] args) throws InterruptedException {

        var leadWorker = new Thread(new Worker("something important"));
        var handyWorker = new Thread(new Helper());

        //comment out the following line and helper will never stop its work
        handyWorker.setDaemon(true);

        leadWorker.start();
        handyWorker.start();

        TimeUnit.SECONDS.sleep(1);
        isWorking = false;
        System.out.println("Ring the bell, work is done!!!");
    }

    public static class Helper implements Runnable {

        @Override
        public void run() {
            while (true) {
                System.out.println(this + ": I'm going to work while the process is alive");
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(this + ": I was interrupted");
                } finally {
                    System.out.println("Finally");
                }
            }
        }

        @Override
        public String toString() {
            return "[Helper-" + Thread.currentThread().getName() + "]";
        }
    }


    public record Worker(String workTitle) implements Runnable {

        @Override
        public void run() {
            while (isWorking) {
                System.out.println(this + ": I'm working on " + workTitle);
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(this + ": Somebody interrupted me");
                }
            }
            System.out.println(this + ": I heard the bell, time to stop");
        }

        @Override
        public String toString() {
            return "[Worker-" + Thread.currentThread().threadId() + "]";
        }
    }
}
