package demo.completable_future.part7;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class AllOf extends Demo {

    @Test
    public void testAllOf() throws InterruptedException, ExecutionException {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[]{
                supplyAsync(() -> sleepAndGet(1, "parallel1")),
                supplyAsync(() -> sleepAndGet(2, "parallel2")),
                supplyAsync(() -> sleepAndGet(3, "parallel3"))
        };

        CompletableFuture<Void> future = CompletableFuture.allOf(futures);
        future.get();

        String result = Stream.of(futures)
                .map(CompletableFuture::join)
                .map(Object::toString)
                .collect(Collectors.joining(" "));

        assertEquals("parallel1 parallel2 parallel3", result);
    }

    @Test
    public void testRunAfterBoth() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
        CompletableFuture<String> future2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));

        CompletableFuture<Void> future = future1
                .runAfterBoth(future2, () -> {});
        future.get();

        String result = Stream.of(future1, future2)
                .map(CompletableFuture::join)
                .map(Object::toString)
                .collect(Collectors.joining(" "));

        assertEquals("parallel1 parallel2", result);
    }
}
