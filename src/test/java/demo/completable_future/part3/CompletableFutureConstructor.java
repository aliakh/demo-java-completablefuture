package demo.completable_future.part3;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompletableFutureConstructor {

    @Test
    public void testConstructor() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();

        assertFalse(future.isDone());

        future.complete("value");

        assertTrue(future.isDone());
        assertEquals("value", future.get());
    }
}
