package com.denisvlem.synchronization.future;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SuppressWarnings("java:S2699")
class CompletableFutureExampleTest {

    /**
     * Выполнение задачи в отдельном потоке
     * Внимание!!!! Для создания отдельного потока используется ForkJoinPool см. вывод теста
     */
    @Test
    void testCompletableFutureAsyncRun() {
        assertThatCode(() ->
            CompletableFuture.runAsync(
                () -> System.out.printf("[%s] is running in a separate thread",
                    Thread.currentThread().getName())
            )).doesNotThrowAnyException();
    }

    /**
     * Вычисление результата в отдельном потоке и обработка этого результата
     * Обработка может даже выполняться в main потоке
     */
    @RepeatedTest(100)
    void testCompletableFutureAsyncSupply() {
        final var response = "response";

        CompletableFuture<String> futureWithResponse = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] supplying from a separate thread%n", Thread.currentThread().getName());
            return response;
        });

        futureWithResponse.thenAccept((result) -> {
            //could be even main thread
            System.out.printf("[%s] accepting in a separate thread%n", Thread.currentThread().getName());
            //Для теста этот assert не имеет значения, т.к. выполняется в другом потоке
            assertThat(result).as("правильный ответ").isEqualTo(response);
        });
    }

    /**
     * Объединение результатов двух операций
     */
    @Test
    void testTaskCombinations() {
        var taskOne = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] is supplying from separate thread%n", Thread.currentThread().getName());
            return "Hello";
        });
        var taskTwo = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] is supplying from separate thread%n", Thread.currentThread().getName());
            return "World";
        });

        var combinedTask = taskOne.thenCombine(taskTwo, (taskOneResult, taskTwoResult) -> {
            System.out.printf("[%s] is combining in separate thread%n", Thread.currentThread().getName());
            return taskOneResult + " " + taskTwoResult;
        });

        combinedTask.thenAccept((result) ->
            System.out.printf("[%s] is accepted in a separate thread%n", Thread.currentThread().getName())
        );
    }

    /**
     * Обработка исключения и возврат значения по-умолчанию
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testExceptionHandling(boolean throwsAnException) {

        var failedFuture = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] throwing an exception in a separate thread%n", Thread.currentThread().getName());
            if (throwsAnException) {
                throw new IllegalStateException("It failed");
            }
            return "It's ok";
        });

        failedFuture.exceptionally((exception) -> {
            System.out.printf("[%s] Exception is processed", Thread.currentThread().getName());
            System.out.println(exception.getMessage());
            return "Default value";
        }).thenAccept(result ->
            System.out.printf("[%s] value:[%s] is processed", Thread.currentThread().getName(), result)
        );
    }
}