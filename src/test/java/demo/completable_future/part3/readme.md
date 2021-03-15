#### Code examples

The no-arg constructor creates an incomplete future.


```
CompletableFuture<String> future = new CompletableFuture<>();
assertFalse(future.isDone());
```


The _newIncompleteFuture_ method creates an incomplete future of the same type as the called _CompletableFuture_ object. You should override this method if you are implementing a subclass of _CompletableFuture_.


```
CompletableFuture<String> future1 = CompletableFuture.completedFuture("value");
assertTrue(future1.isDone());
CompletableFuture<String> future2 = future1.newIncompleteFuture();
assertFalse(future2.isDone());
```


The _supplyAsync_ method creates an incomplete future that is asynchronously completed after it obtains a value from the given _Supplier_.


```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
assertEquals("value", future.get());
```


The _runAsync_ method creates an incomplete future that is asynchronously completed after it runs an action from the given _Runnable_.


```
CompletableFuture<Void> future = CompletableFuture.runAsync(() -> logger.info("action"));
assertNull(future.get());
```


The _completedFuture_ method creates a normally completed future.


```
CompletableFuture<String> future = CompletableFuture.completedFuture("value");
assertTrue(future.isDone());
assertFalse(future.isCompletedExceptionally());
assertEquals("value", future.get());
```


The _failedFuture_ method creates an exceptionally completed future.


```
CompletableFuture<String> future = CompletableFuture.failedFuture(new RuntimeException("exception"));
assertTrue(future.isDone());
assertTrue(future.isCompletedExceptionally());
```
