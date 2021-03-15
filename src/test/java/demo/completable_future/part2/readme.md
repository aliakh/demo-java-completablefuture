#### Code examples

The _whenComplete_ method accepts a nullable result and an exception but can not modify the return value, the stage is
still completed exceptionally.

```
CompletableFuture<String> future = CompletableFuture.<String>failedFuture(new RuntimeException("exception"))
       .whenComplete((value, t) -> {
           if (t == null) {
               logger.info("success: {}", value);
           } else {
               logger.warn("failure: {}", t.getMessage());
           }
       });
assertTrue(future.isDone());
assertTrue(future.isCompletedExceptionally());
```

The _handle_ method transforms a nullable result and an exception and converts the stage from completed exceptionally to
completed normally.

```
CompletableFuture<String> future = CompletableFuture.<String>failedFuture(new RuntimeException("exception"))
       .handle((value, t) -> {
           if (t == null) {
               return value.toUpperCase();
           } else {
               return "failure: " + t.getMessage();
           }
       });
assertTrue(future.isDone());
assertFalse(future.isCompletedExceptionally());
assertEquals("failure: exception", future.get());
```

The _exceptionally_ method transforms an exception and converts the stage from completed exceptionally to completed
normally.

```
CompletableFuture<String> future = CompletableFuture.<String>failedFuture(new RuntimeException("exception"))
       .exceptionally(t -> "failure: " + t.getMessage());
assertTrue(future.isDone());
assertFalse(future.isCompletedExceptionally());
assertEquals("failure: exception", future.get());
```
