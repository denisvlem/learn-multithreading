package com.denisvlem.synchronization.future;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

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
                () -> System.out.printf("[%s] is running in a separate thread%n",
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
        }).join();
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
        ).join();
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
            System.out.printf("[%s] Exception is processed%n", Thread.currentThread().getName());
            System.out.println(exception.getMessage());
            return "Default value";
        }).thenAccept(result ->
            System.out.printf("[%s] value:[%s] is processed%n", Thread.currentThread().getName(), result)
        ).join();
    }

    /**
     * Ожидание всех результатов.
     */
    @Test
    void testWaitingForAllTasks() {

        var taskOne = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] is supplied%n", Thread.currentThread().getName());
            return "Task one";
        });

        var taskTwo = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] is supplied%n", Thread.currentThread().getName());
            return "Task two";
        });

        var taskThree = CompletableFuture.supplyAsync(() -> {
            System.out.printf("[%s] is supplied%n", Thread.currentThread().getName());
            return "Task three";
        });

        var allOfFuture = CompletableFuture.allOf(taskOne, taskTwo, taskThree);

        allOfFuture.thenRun(() -> {
            try {
                String one = taskOne.get();
                String two = taskTwo.get();
                String three = taskThree.get();

                System.out.println(String.join(" ", one, two, three));
            } catch (ExecutionException e) {
                System.out.println("Exception raised");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted");
            }
        }).join();
    }

    /**
     * Выполнение задач в зависимости одна за другой.
     */
    @Test
    void testComposingTasks() {
        CompletableFuture.supplyAsync(() -> {
            var result = "I'm gonna be first completed";
            System.out.printf("[%s] runs for '%s'%n", Thread.currentThread().getName(), result);
            return result;
        }).thenCompose(result -> CompletableFuture.supplyAsync(() -> {
            var secondResult = "I'm gonna be second completed";
            System.out.printf("[%s] runs for '%s'%n", Thread.currentThread().getName(), secondResult);
            return result + "\n" + secondResult;
        })).thenAccept(result ->
            System.out.println("The result is: \n" + result)
        ).join();
    }

    /**
     * Рекурсивная реализация retry с помощью CompletableFuture.
     */
    @RepeatedTest(3)
    void testRetryWithCompletableFuture() {
        CompletableFuture<String> theTask = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.7) {
                throw new IllegalArgumentException("It Failed");
            } else {
                return "Result";
            }
        });

        retry(theTask, 3)
            .thenAccept(result ->
                System.out.println("The result is:" + result)
            ).exceptionally(ex -> {
                System.out.println("Exception raised: " + ex.getMessage());
                return null;
            });
    }

    private CompletableFuture<String> retry(CompletableFuture<String> task, int retry) {
        return task.handle((result, ex) -> {
            if (ex != null & retry > 0) {
                return retry(task, retry - 1);
            } else if (ex != null) {
                throw new CompletionException(ex);
            } else {
                return CompletableFuture.completedFuture(result);
            }
        }).thenCompose(Function.identity());
    }

    /**
     *
     */
    @Test
    void testJoinAndGetDifference() {
        var getTask = CompletableFuture.supplyAsync(() -> "Get task");
        var joinTask = CompletableFuture.supplyAsync(() -> "Join task");

        // .get() требует обработки Checked exception
        try {
            String getResult = getTask.get();
            System.out.println("Get result: " + getResult);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Exception raised: " + e.getMessage());
        }
        String joinResult = joinTask.join();
        System.out.println("Join result: " + joinResult);
    }

    @Test
    void testJoinAndGetDifferenceRaisingException() {
        var throwingGet = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException();
        });

        var throwingJoin = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException();
        });

        assertThatThrownBy(throwingGet::get).isNotNull().satisfies(exception -> {
            assertThat(exception).isInstanceOf(ExecutionException.class);
            assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class);
        });

        //здесь исключение будет unchecked
        assertThatThrownBy(throwingJoin::join).isNotNull().satisfies(exception -> {
            assertThat(exception).isInstanceOf(CompletionException.class);
            assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class);
        });
    }
}