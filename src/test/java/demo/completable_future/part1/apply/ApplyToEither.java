package demo.completable_future.part1.apply;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class ApplyToEither extends Demo {

    @Test
    public void testApplyToEither() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
        CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));

        CompletionStage<String> stage = stage1.applyToEither(stage2,
                s -> s.toUpperCase());

        assertEquals("PARALLEL1", stage.toCompletableFuture().get());
    }
}
