#### Code examples

The static _allOf_ method returns a new incomplete future that completes when _all_ of the _given_ _CompletableFutures_ complete. Note that the result type of this method is _CompletableFuture&lt;Void>_ and you should get results of each future by reading them individually.


```
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
```


Compare the previous method with the _runAfterBoth_ method that returns a new incomplete future that completes when _all_ of the _two_ _CompletableFutures_ complete.


```
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
```


The static _anyOf_ method returns a new incomplete future that is completed when _any_ of the given _CompletableFutures_ complete. Note that the result type of this method is _CompletableFuture&lt;Object>_ and you should cast the result to the required type manually.


```
CompletableFuture<Object> future = CompletableFuture.anyOf(
       supplyAsync(() -> sleepAndGet(1, "parallel1")),
       supplyAsync(() -> sleepAndGet(2, "parallel2")),
       supplyAsync(() -> sleepAndGet(3, "parallel3"))
);

assertEquals("parallel1", future.get());
```


Compare the previous method with the _applyToEither_ that returns a new incomplete future that is completed when _any_ of the _two_ _CompletableFutures_ complete.


```
CompletableFuture<String> future1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletableFuture<String> future2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));

CompletableFuture<String> future = future1
       .applyToEither(future2, value -> value);

assertEquals("parallel1", future.get());
```
