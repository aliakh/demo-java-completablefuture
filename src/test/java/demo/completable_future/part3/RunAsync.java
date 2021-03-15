package demo.completable_future.part3;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNull;

public class RunAsync extends Demo {

    @Test
    public void testRunAsync() throws InterruptedException, ExecutionException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> logger.info("action"));
        assertNull(future.get());
    }
}
