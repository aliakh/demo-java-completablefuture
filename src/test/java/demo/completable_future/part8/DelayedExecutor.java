package demo.completable_future.part8;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DelayedExecutor extends Demo {

    @Test
    public void testDelayedExecutor() throws InterruptedException, ExecutionException {
        CompletableFuture<Object> future = new CompletableFuture<>();
        assertFalse(future.isDone());

        future.completeAsync(() -> "value", CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
        assertFalse(future.isDone());

        TimeUnit.SECONDS.sleep(2);

        assertTrue(future.isDone());
        assertEquals("value", future.get());
    }
}
