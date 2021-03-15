#### Code examples

The _isDone_ method returns _true_ because the future is completed.


```
CompletableFuture<String> future = CompletableFuture.completedFuture("value");
assertTrue(future.isDone());
assertFalse(future.isCompletedExceptionally());
assertFalse(future.isCancelled());
assertEquals("value", future.get());
```


The _isCompletedExceptionally_ method returns _true_ because the future is completed exceptionally. Note that a future _completed exceptionally_ is also considered _completed_.


```
CompletableFuture<String> future = CompletableFuture.failedFuture(new RuntimeException("exception"));
assertTrue(future.isDone());
assertTrue(future.isCompletedExceptionally());
assertFalse(future.isCancelled());
```


The _isCancelled_ method returns _true_ because the future is canceled. Note that a _canceled_ future is also considered _completed exceptionally_ and _completed_.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
assertFalse(future.isDone());
assertFalse(future.isCompletedExceptionally());
assertFalse(future.isCancelled());
future.cancel(true);
assertTrue(future.isDone());
assertTrue(future.isCompletedExceptionally());
assertTrue(future.isCancelled());
```
