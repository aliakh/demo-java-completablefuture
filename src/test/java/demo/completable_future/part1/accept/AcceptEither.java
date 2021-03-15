package demo.completable_future.part1.accept;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertNull;

public class AcceptEither extends Demo {

    @Test
    public void testAcceptEither() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
        CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));

        CompletionStage<Void> stage = stage1.acceptEither(stage2,
                s -> logger.info("consumes the first: {}", s));

        assertNull(stage.toCompletableFuture().get());
    }
}
