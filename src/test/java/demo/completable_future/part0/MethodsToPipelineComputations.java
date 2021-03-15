package demo.completable_future.part0;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class MethodsToPipelineComputations extends Demo {

    // area = π * r^2
    @Test
    public void test() {
        CompletableFuture<Double> pi = CompletableFuture.supplyAsync(() -> Math.PI);
        CompletableFuture<Integer> radius = CompletableFuture.supplyAsync(() -> 1);

        CompletableFuture<Void> area = radius
                .thenApply(r -> r * r)
                .thenCombine(pi, (multiplier1, multiplier2) -> multiplier1 * multiplier2)
                .thenAccept(a -> logger.info("area: {}", a))
                .thenRun(() -> logger.info("operation completed"));

        area.join();
    }
}
