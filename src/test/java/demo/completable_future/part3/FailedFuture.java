package demo.completable_future.part3;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertTrue;

public class FailedFuture extends Demo {

    @Test
    public void testCompletedFuture() {
        CompletableFuture<String> future = CompletableFuture.failedFuture(new RuntimeException("exception"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void testCompletedStage() {
        CompletionStage<String> future = CompletableFuture.failedStage(new RuntimeException("exception"));

        assertTrue(future.toCompletableFuture().isDone());
        assertTrue(future.toCompletableFuture().isCompletedExceptionally());
    }
}

