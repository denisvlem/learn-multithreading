package com.denisvlem;

import java.util.concurrent.TimeUnit;

public class BlockingSetExample {

    public static void main(String[] args) {
        var criticalSection = new CriticalSection();
        var hardWorkingThread = new Thread(criticalSection::doInCriticalSection, "HardWorkThread");
        var blockedThread = new Thread(criticalSection::doInAnotherCriticalSection, "BlockedThread");

        var anotherCriticalSection = new CriticalSection();
        //anotherCriticalSection - другой объект (другой монитор), ничто поток блокировать не будет
        var notBlockedThread = new Thread(anotherCriticalSection::doInAnotherCriticalSection, "NotBlockedThread");

        hardWorkingThread.start();
        blockedThread.start();
        notBlockedThread.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        final String messageFormat = "[%s] is in the state: [%s]%n";
        System.out.printf(
            messageFormat,
            blockedThread.getName(), blockedThread.getState()
        );
        System.out.printf(
            messageFormat,
            hardWorkingThread.getName(), hardWorkingThread.getState()
        );
        System.out.printf(
            messageFormat,
            notBlockedThread.getName(), notBlockedThread.getState()
        );
    }

    public static class CriticalSection {


        public void doInCriticalSection() {
            System.out.printf("[%s] Enter critical section%n", Thread.currentThread().getName());
            synchronized (this) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.printf("[%s] Exit critical section%n", Thread.currentThread().getName());
            }
        }

        public void doInAnotherCriticalSection() {
            System.out.printf("[%s] Enter critical section%n", Thread.currentThread().getName());
            synchronized (this) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.printf("[%s] Exit critical section%n", Thread.currentThread().getName());
            }
        }
    }
}
