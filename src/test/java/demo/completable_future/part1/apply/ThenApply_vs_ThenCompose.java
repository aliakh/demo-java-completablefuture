package demo.completable_future.part1.apply;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class ThenApply_vs_ThenCompose extends Demo {

    @Test
    public void testThenApplyFast() throws Exception {
        CompletableFuture<Integer> future = supplyAsync(() -> 2)
                .thenApply(i -> i + 3); // Function<Integer, Integer>
        assertEquals(5, future.get().intValue());
    }

    @Test
    public void testThenApplySlow() throws Exception {
        CompletableFuture<CompletableFuture<Integer>> future1 = supplyAsync(() -> 2)
                .thenApply(i -> supplyAsync(() -> i + 3)); // Function<Integer, CompletableFuture<Integer>>
        CompletableFuture<Integer> future2 = future1.get(); // blocking
        assertEquals(5, future2.get().intValue());
    }

    @Test
    public void testThenCompose() throws Exception {
        CompletableFuture<Integer> future = supplyAsync(() -> 2)
                .thenCompose(i -> supplyAsync(() -> i + 3)); // Function<Integer, CompletionStage<Integer>>
        assertEquals(5, future.get().intValue());
    }
}
