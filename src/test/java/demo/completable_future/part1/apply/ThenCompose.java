package demo.completable_future.part1.apply;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class ThenCompose extends Demo {

    @Test
    public void testThenCompose() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("sequential1"));

        CompletionStage<String> stage = stage1.thenCompose(
                s -> supplyAsync(() -> sleepAndGet((s + " " + "sequential2").toUpperCase())));

        assertEquals("SEQUENTIAL1 SEQUENTIAL2", stage.toCompletableFuture().get());
    }
}
