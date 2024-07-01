package com.denisvlem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ThreadInterruptionTest {

    @Test
    void testInterruptedSynchronize() {

        var interruptedWorker = new ThreadInterruption.InterruptedWorker();
        interruptedWorker.start();

        assertThatCode(interruptedWorker::join).doesNotThrowAnyException();
        assertThat(interruptedWorker.isInterrupted()).isTrue();
    }

    @Test
    void testInterruptedWithReentrantLock() {

        var interruptedWorker = new ThreadInterruption.InterruptedLockWorker();
        interruptedWorker.start();

        assertThatCode(interruptedWorker::join).doesNotThrowAnyException();
        assertThat(interruptedWorker.isInterrupted()).isTrue();
    }

}