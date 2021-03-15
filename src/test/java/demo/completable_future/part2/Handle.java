package demo.completable_future.part2;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class Handle extends Demo {

    @Test
    public void testHandleSuccess() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value")
                .handle((value, t) -> {
                    if (t == null) {
                        return value.toUpperCase();
                    } else {
                        return t.getMessage();
                    }
                });

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("VALUE", future.get());
    }

    @Test
    public void testHandleError() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.<String>failedFuture(new RuntimeException("exception"))
                .handle((value, t) -> {
                    if (t == null) {
                        return value.toUpperCase();
                    } else {
                        return "failure: " + t.getMessage();
                    }
                });

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("failure: exception", future.get());
    }
}
