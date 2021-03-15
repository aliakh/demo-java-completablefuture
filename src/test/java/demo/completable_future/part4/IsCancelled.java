package demo.completable_future.part4;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsCancelled extends Demo {

    @Test
    public void testIsCancelledTrue() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertFalse(future.isCancelled());
        assertEquals("value", future.get());
    }

    @Test
    public void testIsCancelledFalse() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));

        assertFalse(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertFalse(future.isCancelled());

        future.cancel(true);

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
        assertTrue(future.isCancelled());
    }
}