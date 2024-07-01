package com.denisvlem.synchronization.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinDemo {

    public static void main(String[] args) {
        try (ForkJoinPool pool = ForkJoinPool.commonPool()) {
            Long total = pool.invoke(new RecursiveSum(0, Integer.MAX_VALUE));
            System.out.println(total);
        }
    }

    public static class RecursiveSum extends RecursiveTask<Long> {

        private final int left;
        private final int right;

        public RecursiveSum(int left, int right) {
            this.left = left;
            this.right = right;
        }
        @Override
        protected Long compute() {
            if(right - left <= 100_000) {
                long total = 0;
                for (int i = left; i < right; ++i) {
                    total += i;
                }
                return total;
            } else {
                int mid = (left + right) / 2;
                var leftTask = new RecursiveSum(left, mid);
                var rightTask = new RecursiveSum(mid + 1, right);

                leftTask.fork();
                rightTask.fork();

                return rightTask.join() + leftTask.join();
            }

        }
    }

}
