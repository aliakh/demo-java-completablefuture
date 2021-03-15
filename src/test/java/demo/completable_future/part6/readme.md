#### Code examples

The _get_ method waits until the future is completed and returns the result. This method can throw the checked _InterruptedException_, _ExecutionException_ exceptions.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
assertEquals("value", future.get());
```


The _join_ method waits until the future is completed and returns the result. This method can not throw checked exceptions (it can be used as a method reference, for example, in Streams API).


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
assertEquals("value", future.join());
```


The _get(timeout, timeUnit)_ method waits for at most the given time and throws the checked _TimeoutException_ exception because the timeout occurs earlier than the future is completed.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet(2, "value"));
future.get(1, TimeUnit.SECONDS); // throws TimeoutException
```


The _getNow_ method does not wait and immediately returns the default value because the future is not completed. Note that this method does not cause the _CompletableFuture_ to complete.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
assertEquals("default", future.getNow("default"));
assertFalse(future.isDone());
```
