#### Code examples

The _thenApply_ method creates a new stage, that upon completion transforms the result of the single previous stage by
the given _Function_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("single"));
CompletionStage<String> stage = stage1.thenApply(
       s -> s.toUpperCase());
assertEquals("SINGLE", stage.toCompletableFuture().get());
```

The _thenCompose_ method creates a new stage, that upon completion also transforms the result of the single previous
stage by the given _Function_. This method is similar to the _thenApply_ method described above. The difference is that
the result of this _Function_ is a subclass of _CompletionStage_, which is useful when a transformation is a slow
operation that is reasonable to execute in a separate stage (possible asynchronously).

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("sequential1"));
CompletionStage<String> stage = stage1.thenCompose(
       s -> supplyAsync(() -> sleepAndGet((s + " " + "sequential2").toUpperCase())));
assertEquals("SEQUENTIAL1 SEQUENTIAL2", stage.toCompletableFuture().get());
```

The _applyToEither_ method creates a new stage, that upon completion transforms the first result of the previous two
stages by the given _Function_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));
CompletionStage<String> stage = stage1.applyToEither(stage2,
       s -> s.toUpperCase());
assertEquals("PARALLEL1", stage.toCompletableFuture().get());
```

The _thenCombine_ method creates a new stage, that upon completion transforms the two results of the previous two stages
by the given _BiFunction_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet("parallel2"));
CompletionStage<String> stage = stage1.thenCombine(stage2,
       (s1, s2) -> (s1 + " " + s2).toUpperCase());
assertEquals("PARALLEL1 PARALLEL2", stage.toCompletableFuture().get());
```

The _thenAccept_ method creates a new stage, that upon completion consumes the single previous stage by the given _
Consumer_.

```
CompletableFuture<String> stage1 = supplyAsync(() -> sleepAndGet("single"));
CompletionStage<Void> stage = stage1.thenAccept(
       s -> logger.info("consumes the single: {}", s));
assertNull(stage.toCompletableFuture().get());
```

The _acceptEither_ method creates a new stage, that upon completion consumes the first result of the previous two stages
by the given _Consumer_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));
CompletionStage<Void> stage = stage1.acceptEither(stage2,
       s -> logger.info("consumes the first: {}", s));
assertNull(stage.toCompletableFuture().get());
```

The _thenAcceptBoth_ method creates a new stage, that upon completion consumes the two results of the previous two
stages by the given _BiConsumer_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));
CompletionStage<Void> stage = stage1.thenAcceptBoth(stage2,
       (s1, s2) -> logger.info("consumes both: {} {}", s1, s2));
assertNull(stage.toCompletableFuture().get());
```

The _thenRun_ method creates a new stage, that upon completion of the single previous stage runs the given _Runnable_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("single"));
CompletionStage<Void> stage = stage1.thenRun(
       () -> logger.info("runs after the single"));
assertNull(stage.toCompletableFuture().get());
```

The _runAfterEither_ method creates a new stage, that upon completion of the first of the previous two stages, runs the
given _Runnable_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));
CompletionStage<Void> stage = stage1.runAfterEither(stage2,
       () -> logger.info("runs after the first"));
assertNull(stage.toCompletableFuture().get());
```

The _runAfterBoth_ method creates a new stage, that upon completion of the previous two stages, runs the given _
Runnable_.

```
CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet(1, "parallel1"));
CompletionStage<String> stage2 = supplyAsync(() -> sleepAndGet(2, "parallel2"));
CompletionStage<Void> stage = stage1.runAfterBoth(stage2,
       () -> logger.info("runs after both"));
assertNull(stage.toCompletableFuture().get());
```
