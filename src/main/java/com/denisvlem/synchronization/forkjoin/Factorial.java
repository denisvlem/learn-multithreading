package com.denisvlem.synchronization.forkjoin;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Factorial {

    public BigInteger compute(long n) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        var result = forkJoinPool.invoke(new FactorialRecursive(n));
        forkJoinPool.shutdown();
        return result;
    }

    public BigInteger computeLinear(long n) {
        if(0 == n || 1 == n) {
            return BigInteger.ONE;
        }
        return BigInteger.valueOf(n).multiply(computeLinear(n - 1));
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
        final long n = 1000;

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

