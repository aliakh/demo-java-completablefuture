package demo.completable_future.part5;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class Cancel extends Demo {

    @Test
    public void testCancel() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();

        assertFalse(future.isDone());
        assertFalse(future.isCancelled());

        boolean isCanceled = future.cancel(false);

        assertTrue(isCanceled);
        assertTrue(future.isDone());
        assertTrue(future.isCancelled());

        try {
            future.get();
            fail();
        } catch (CancellationException e) {
            assertTrue(true);
        }
    }
}
