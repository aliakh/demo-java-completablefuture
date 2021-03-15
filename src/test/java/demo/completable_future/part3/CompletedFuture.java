package demo.completable_future.part3;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompletedFuture extends Demo {

    @Test
    public void testCompletedFuture() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value", future.get());
    }

    @Test
    public void testCompletedStage() throws InterruptedException, ExecutionException {
        CompletionStage<String> future = CompletableFuture.completedStage("value");

        assertTrue(future.toCompletableFuture().isDone());
        assertFalse(future.toCompletableFuture().isCompletedExceptionally());
        assertEquals("value", future.toCompletableFuture().get());
    }
}

