package demo.completable_future.part1.run;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertNull;

public class RunAfterEither extends Demo {

    @Test
    public void testRunAfterEither() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
        CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));

        CompletionStage<Void> stage = stage1.runAfterEither(stage2,
                () -> logger.info("runs after the first"));

        assertNull(stage.toCompletableFuture().get());
    }
}
