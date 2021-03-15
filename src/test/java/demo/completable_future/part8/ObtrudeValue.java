package demo.completable_future.part8;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObtrudeValue extends Demo {

    @Test
    public void testObtrudeValue1() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value1");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value1", future.get());

        future.obtrudeValue("value2");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value2", future.get());
    }

    @Test
    public void testObtrudeValue2() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.failedFuture(new RuntimeException("error"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());

        future.obtrudeValue("value");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value", future.get());
    }
}
