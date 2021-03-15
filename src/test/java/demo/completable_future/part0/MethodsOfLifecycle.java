package demo.completable_future.part0;

import demo.completable_future.common.Demo;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MethodsOfLifecycle extends Demo {

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture<String> future = new CompletableFuture<>(); // creating an incomplete future

        executorService.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            future.complete("value"); // completing the incomplete future
            return null;
        });

        while (!future.isDone()) { // checking the future for completion
            TimeUnit.SECONDS.sleep(2);
        }

        String result = future.get(); // reading value of the completed future
        logger.info("result: {}", result);

        executorService.shutdown();
    }
}
