package demo.completable_future.part6;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Get_with_timeout extends Demo {

    @Test
    public void testGetWithTimeoutSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet(2, "value"));
        assertEquals("value", future.get(3, TimeUnit.SECONDS));
    }

    @Test
    public void testGetWithTimeoutFailure() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet(2, "value"));
        try {
            future.get(1, TimeUnit.SECONDS);
            fail();
        } catch (TimeoutException e) {
            assertTrue(true);
        }
    }
}
