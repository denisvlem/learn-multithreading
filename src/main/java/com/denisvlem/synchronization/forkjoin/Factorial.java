package com.denisvlem.synchronization.forkjoin;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Factorial {

    public BigInteger compute(long n) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            return forkJoinPool.invoke(new FactorialRecursive(n));
        }
    }

    public BigInteger computeLinear(long n) {
        if(0 == n || 1 == n) {
            return BigInteger.ONE;
        }

        var result = BigInteger.ONE;
        for (var i = BigInteger.valueOf(1); i.compareTo(BigInteger.valueOf(n)) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(i);
        }
        return result;
    }

    private static class FactorialRecursive extends RecursiveTask<BigInteger> {

        private final long n;

        public FactorialRecursive(long n) {
            this.n = n;
        }

        @Override
        protected BigInteger compute() {
            if (0 == n || 1 == n) {
                return BigInteger.ONE;
            } else {
                var prev = new FactorialRecursive(n - 1);

                prev.fork();

                return prev.join().multiply(BigInteger.valueOf(n));
            }
        }
    }

    public static void main(String... args) {
        var factorial = new Factorial();
        final long n = 100;

        var beforeLinear = System.nanoTime();
        var linearResult = factorial.computeLinear(n);
        var afterLinear = System.nanoTime();

        var beforeForJoin = System.nanoTime();
        var forkJoinResult = factorial.compute(n);
        var afterForkJoin = System.nanoTime();
        System.out.println(linearResult);
        System.out.println(forkJoinResult);

        System.out.println("Linear: \t" + (afterLinear - beforeLinear) + " ns");
        System.out.println("ForkJoin: \t" + (afterForkJoin - beforeForJoin) + " ns");
    }
}

