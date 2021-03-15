package demo.completable_future.part8;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class DefaultExecutor extends Demo {

    @Test
    public void testDefaultExecutor() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");
        System.out.println(future.defaultExecutor());
    }
}
