#### Code examples

The _complete_ method completes the future normally because it is not completed.


```
CompletableFuture<String> future = new CompletableFuture<>();
assertFalse(future.isDone());
boolean hasCompleted = future.complete("value");
assertTrue(hasCompleted);
assertTrue(future.isDone());
assertEquals("value", future.get());
```


The _completeAsync_ method asynchronously completes the future normally with the result of the given Supplier.


```
CompletableFuture<String> future1 = new CompletableFuture<>();
assertFalse(future1.isDone());
CompletableFuture<String> future2 = future1.completeAsync(() -> "value");
sleep(1);
assertTrue(future2.isDone());
assertEquals("value", future2.get());
```


The _completeOnTimeout​_ method completes the future normally with the default value because it is not completed before the given timeout.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet(2, "value"))
       .completeOnTimeout("default", 1, TimeUnit.SECONDS);
assertEquals("default", future.get());
```


The _orTimeout_ method completes the future exceptionally because it is not completed before the given timeout.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet(2, "value"))
       .orTimeout(1, TimeUnit.SECONDS);
future.get(); // throws ExecutionException caused by TimeoutException
```


The _completeExceptionally_ method completes the future exceptionally because it is not completed.


```
CompletableFuture<String> future = new CompletableFuture<>();
assertFalse(future.isDone());
assertFalse(future.isCompletedExceptionally());
boolean hasCompleted = future.completeExceptionally(new RuntimeException("exception"));
assertTrue(hasCompleted);
assertTrue(future.isDone());
assertTrue(future.isCompletedExceptionally());
```


The _cancel_ method cancels the future (completes it exceptionally) because it is not completed.


```
CompletableFuture<String> future = new CompletableFuture<>();
assertFalse(future.isDone());
assertFalse(future.isCancelled());
boolean isCanceled = future.cancel(false);
assertTrue(isCanceled);
assertTrue(future.isDone());
assertTrue(future.isCancelled());
future.get(); // throws CancellationException
```
