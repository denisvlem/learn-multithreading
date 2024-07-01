package com.denisvlem.synchronization.join;

import java.util.concurrent.TimeUnit;

public class ThreadJoinDemo {

    public static void main(String[] args) throws InterruptedException {

        final String currentThreadName = Thread.currentThread().getName();
        System.out.printf("[%s]: Started the main work...\n", currentThreadName);
        var workerOne = new Worker(3);
        var workerTwo = new Worker(7);

        System.out.printf("[%s]: Starting additional workers...\n", currentThreadName);
        workerOne.start();
        workerTwo.start();
        new Worker(0).start(); //is not to wait for

        workerOne.interrupt();
        System.out.printf("[%s]: Here goes the work...\n", currentThreadName);
        TimeUnit.SECONDS.sleep(2);

        //it is always false
        System.out.printf("[%s]: workerOne.isInterrupted() = " + workerOne.isInterrupted() + "\n", currentThreadName);

        System.out.printf("[%s]: Waiting for the others...\n", currentThreadName);
        workerOne.join();
        workerTwo.join();

        System.out.printf("[%s]: End of all work...\n", currentThreadName);
    }

    public static class Worker extends Thread {

        private final int sleep;

        public Worker(int sleep) {
            this.sleep = sleep;
        }

        @Override
        public void run() {
            System.out.println(this + ": has started its work...");
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                //it is always false in the catch
                Thread.currentThread().interrupt();
                System.out.println(this + ": isInterrupted() = " + isInterrupted());
                System.out.println(this + ": has been interrupted...");
            }
            System.out.println(this + ": has completed its work...");
        }

        @Override
        public String toString() {
            return "[" + Thread.currentThread().getName() + "]";
        }
    }
}
