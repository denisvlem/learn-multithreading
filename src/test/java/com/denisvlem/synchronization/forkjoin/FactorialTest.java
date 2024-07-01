package com.denisvlem.synchronization.forkjoin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class FactorialTest {

    private final Factorial factorial = new Factorial();

    @Test
    void givenN_whenFactorialRequested_shouldReturnFactorial() {
        Assertions.assertEquals(BigInteger.ONE, factorial.compute(0));
        Assertions.assertEquals(BigInteger.ONE, factorial.computeLinear(0));

        Assertions.assertEquals(BigInteger.ONE, factorial.compute(1));
        Assertions.assertEquals(BigInteger.ONE, factorial.computeLinear(1));

        Assertions.assertEquals(BigInteger.valueOf(3628800), factorial.computeLinear(10));
        Assertions.assertEquals(BigInteger.valueOf(3628800), factorial.compute(10));

        var expectedFor51 = new BigInteger("15511187532873822802242430164693032110" +
            "63259720016986112000000000000");
        Assertions.assertEquals(expectedFor51, factorial.computeLinear(51));
        Assertions.assertEquals(expectedFor51, factorial.compute(51));
    }
}