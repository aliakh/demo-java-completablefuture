# Asynchronous programming in Java with CompletableFuture


## Introduction

The CompletableFuture API is a high-level API for asynchronous programming in Java. This API supports _pipelining_ (also known as _chaining_ or _combining_) of multiple asynchronous computations into a single result without the mess of nested callbacks (“callback hell“). This API also is an implementation of the _future/promise_ concurrency constructs in Java.

Since Java 5 there is a much simpler API for asynchronous programming: the _Future_ interface and its base implementation, the _FutureTask_ class. The _Future_ interface represents the _result_ of asynchronous computation and has only a few methods:



*   to check if a task is completed or canceled
*   to cancel a task
*   to wait for a task to complete (if necessary) and then get its result

However, the _Future_ interface has significant limitations in building non-trivial asynchronous computations: 



*   it is impossible to register a callback for a future competition
*   it is impossible to pipeline futures in a non-blocking manner
*   it is impossible to manually complete a future

To overcome these limitations, Java 8 added (and Java 9 and Java 12 updated) the _CompletionStage_ interface and its base implementation, the _CompletableFuture_ class. These classes allow building efficient and fluent multi-stage asynchronous computations.

However, the CompletableFuture API is not simple. The _CompletionStage_ interface has 43 public methods. The _CompletableFuture_ class implements 5 methods from the _Future_ interface, 43 methods from the _CompletionStage_ interface, and has 30 of its public methods. This article describes only the most useful methods of the CompletableFuture API. 


## Futures and promises

Future/promise are the high-level concurrency constructs that decouple a value (a future) from the way it is computed (a promise). That allows writing more fluent concurrent programs that transfer objects between threads without using any explicit synchronization mechanisms. The future/promise constructs are often used when multiple threads work on different tasks, and the results need to be combined by the main thread. 

Implementations of future/promise exist in many programming languages:



*   JаvаScript: _Promise_
*   Java: _java.util.concurrent.Future_, _java.util.concurrent.CompletableFuture_
*   Scala: _scala.concurrent.Future_
*   C#: _Task_, _TaskCompletionSource_

Concepts of futures and promises are sometimes used interchangeably. In reality, they are separate objects that encapsulate the two different sets of functionality. 

A _future_ is a read-only object to encapsulate a value that may not be available yet but will be provided at some point. The future is used by a consumer to retrieve the result which was computed. A _promise_ is a writable, single-assignment object to guarantee that some task will compute some result and make it available in the future. The promise is used by a producer to store the success value or exception in the corresponding future. 

The following workflow example can help you to understand the idea of future/promise. A consumer sends a task to a producer to execute it asynchronously. The producer creates a promise that starts the given task. From the promise, the producer extracts a future and sends it to the consumer. The consumer receives the future that is not completed and waits for its completion. 

The consumer can call a blocking getter of the future to wait for the value to be available. If the future has already been completed, the call to the getter will return the result immediately. Otherwise, the call to the getter will wait until the future is finished. (Also, the consumer can use a non-blocking checking method to identify whether the future has already been completed or not).

Once the task has finished, the producer sets the value of the promise, and the future becomes available. But when the task fails, the future will contain an exception instead of a success value. In this case, when the consumer calls the getter method, the exception in the future will be thrown. 

![future and promise workflow](/images/future_and_promise_workflow.png)

One of the important features of future/promise implementations is the ability to chain tasks together. The idea is that when one future/promise is finished, another future/promise is created that takes the result of the previous one. This means that the consumer is not blocked by calling the getter on a future. Once the future is completed, the result of the previous task is automatically passed to the next task in the chain. Compared to callbacks, this allows writing more fluent asynchronous code that supports the composition of nested success and failure handlers without ”callback hell”.

In Java, the _Future_ interface represents a future: it has the _isDone_ method to check if the task is completed and the _get_ method to wait for the task to complete and get its result. The _CompletableFuture_ class represents a promise: it has the _complete_ and _completeExceptionally_ methods to set the result of the task with a successful value or with an exception. However, the _CompletableFuture_ class also implements the _Future_ interface allowing it to be used as a future. 

![Java futures class diagram](/images/Java_futures_class_diagram.png)


## CompletableFuture in practice

The following code example can help you to understand the use of the _CompletableFuture_ class as a future/promise implementation in Java.

Let’s implement the following simplified multi-stage workflow. First, we need to call two long-running methods that return a product price in the EUR and the EUR/USD exchange rate. Then, we need to calculate the net product price from the results of these methods. Then, we need to call the third long-running method that takes the net product price and returns the tax amount. Finally, we need to calculate the gross product price from the net product price and the tax amount.

Implementation of this workflow is divided into the following tasks:



1. to get the product price in the EUR (a slow task)
2. to get the EUR/USD exchange rate (a slow task)
3. to calculate the net product price (a fast task, depends on tasks 1, 2)
4. to get the tax amount (a slow task, depends on tasks 3)
5. to calculate the gross product price (a fast task, depends on tasks 3, 4)

Note that not all tasks are similar. Some of them are fast (they should be executed synchronously), and some of them are slow (they should be executed asynchronously). Some of them are independent (they can be executed in parallel), and some of them depend on the results of previous tasks (they have to be executed sequentially).

The mentioned workflow is implemented below in three programming styles: synchronous, asynchronous based on the _Future_ interface, and asynchronous based on the _CompletableFuture_ class.

>In _synchronous_ programming, the main thread starts an axillary task and blocks until this task is finished. When the axillary task is completed, the main thread continues the main task.

>In _asynchronous_ programming, the main thread starts an axillary task in a worker thread and continues its task. When the worker thread completes the auxiliary task, it notifies the main thread (for example, with a callback call).

The advantage of the synchronous implementation is the simplest and most reliable code. The disadvantage of this implementation is the longest execution time (because all tasks run sequentially).


```
logger.info("this task started");

int netAmountInUsd = getPriceInEur() * getExchangeRateEurToUsd(); // blocking
float tax = getTax(netAmountInUsd); // blocking
float grossAmountInUsd = netAmountInUsd * (1 + tax);

logger.info("this task finished: {}", grossAmountInUsd);
logger.info("another task started");
```


The advantage of the asynchronous implementation based on the _Future_ interface is shorter execution time (because some tasks run in parallel). The disadvantage of this implementation is the most complicated code (because the _Future_ interface lacks methods for tasks pipelining).


```
logger.info("this task started");

Future<Integer> priceInEur = executorService.submit(this::getPriceInEur);
Future<Integer> exchangeRateEurToUsd = executorService.submit(this::getExchangeRateEurToUsd);

while (!priceInEur.isDone() || !exchangeRateEurToUsd.isDone()) { // non-blocking
   Thread.sleep(100);
   logger.info("another task is running");
}

int netAmountInUsd = priceInEur.get() * exchangeRateEurToUsd.get(); // actually non-blocking
Future<Float> tax = executorService.submit(() -> getTax(netAmountInUsd));

while (!tax.isDone()) { // non-blocking
   Thread.sleep(100);
   logger.info("another task is running");
}

float grossAmountInUsd = netAmountInUsd * (1 + tax.get()); // actually non-blocking

logger.info("this task finished: {}", grossAmountInUsd);
logger.info("another task is running");
```


The advantage of the asynchronous implementation based on the _CompletableFuture_ class is shorter execution time (because some tasks run in parallel) _and_ more fluent code. The disadvantage of this implementation is that the more advanced CompletableFuture API is at the same time harder to learn.


```
CompletableFuture<Integer> priceInEur = CompletableFuture.supplyAsync(this::getPriceInEur);
CompletableFuture<Integer> exchangeRateEurToUsd = CompletableFuture.supplyAsync(this::getExchangeRateEurToUsd);

CompletableFuture<Integer> netAmountInUsd = priceInEur
       .thenCombine(exchangeRateEurToUsd, (price, exchangeRate) -> price * exchangeRate);

logger.info("this task started");

netAmountInUsd
       .thenCompose(amount -> CompletableFuture.supplyAsync(() -> amount * (1 + getTax(amount))))
       .whenComplete((grossAmountInUsd, throwable) -> {
           if (throwable == null) {
               logger.info("this task finished: {}", grossAmountInUsd);
           } else {
               logger.warn("this task failed: {}", throwable.getMessage());
           }
       }); // non-blocking

logger.info("another task started");
```



## The CompletionStage interface

The _CompletionStage_ interface represents a stage in a multi-stage (possibly asynchronous) computation where stages can be _forked, chained, and joined_. 

This interface specifies _pipelining_ of the future/promise implementation in the CompletableFuture API:



1. Each stage performs a computation. A stage can or can not require arguments. A stage can either compute a value (returns a result) or performs an action (returns no result). 
2. Stages can be chained in a pipeline. A stage can be started by finishing a single previous stage (or two previous stages) in the pipeline. A stage finishes when its computation is completed. Finishing a stage can start a single next stage in the pipeline. 
3. A stage can be executed synchronously or asynchronously. The appropriate execution type should be selected depending on the parameters of the computation.

The methods of the _CompletionStage_ interface can be divided into two groups according to their purpose:



*   methods to pipeline computations
*   methods to handle exceptions

![methods of the CompletionStage interface](/images/methods_of_the_CompletionStage_interface.png)

The _CompletionStage_ interface contains only methods for stages pipelining. This interface does not contain methods for other parts of stages workflow: creating, checking, completing, reading. This functionality is delegated to the _CompletableFuture_ class - the main implementation of the _CompletionStage_ interface.


### Methods to pipeline computations

The _CompletionStage_ interface has 43 public methods, most of which follow three clear naming patterns.

The first naming pattern explains _how the new stage starts_:



*   if a method name has fragment “then“, then the new stage starts after completion of a single previous stage
*   if a method name has fragment “either“, then the new stage starts after completion of the first of two previous stages
*   if a method name has fragment “both“, then the new stage starts after completion of both of two previous stages

The second naming pattern explains _what computations perform the new stage_:



*   if a method name has fragment “apply“, then the new stage transforms an argument by the given _Function_
*   if a method name has fragment “accept“, then the new stage consumes an argument by the given _Consumer_
*   if a method name has fragment “run“, then the new stage runs an action by the given _Runnable_

>If the new stage depends on both of the two previous stages, it uses _BiFunction_ instead of _Function_ and _BiConsumer_ instead of _Consumer_.

Summary of methods to pipeline computations:


<table>
  <tr>
   <td>
   </td>
   <td><em>Function</em>
<p>
(takes one argument and returns a result)
   </td>
   <td><em>Consumer</em>
<p>
(takes one argument and returns no result)
   </td>
   <td><em>Runnable</em>
<p>
(takes no argument and returns no result)
   </td>
  </tr>
  <tr>
   <td>then
   </td>
   <td><em>thenApply, thenCompose</em>
   </td>
   <td><em>thenAccept</em>
   </td>
   <td><em>thenRun</em>
   </td>
  </tr>
  <tr>
   <td>either
   </td>
   <td><em>applyToEither</em>
   </td>
   <td><em>acceptEither</em>
   </td>
   <td><em>runAfterEither</em>
   </td>
  </tr>
  <tr>
   <td>
   </td>
   <td>
   </td>
   <td>
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td>
   </td>
   <td><em>BiFunction</em>
<p>
(takes two arguments and returns a result)
   </td>
   <td><em>BiConsumer</em>
<p>
(takes two arguments and returns no result)
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td>both
   </td>
   <td><em>thenCombine</em>
   </td>
   <td><em>thenAcceptBoth</em>
   </td>
   <td><em>runAfterBoth</em>
   </td>
  </tr>
</table>


>If methods accept a functional interface that does not return a result (_Consumer_, _BiConsumer_, _Runnable_), it is used to perform a computation with _side-effects_. Such methods can signal that the computation has been completed either with a result or with an exception.

The third naming pattern explains _what thread executes the new stage_:



*   if a method has fragment “something(...)“, then the new stage is executed by _the default facility_ (that can be synchronous or asynchronous)
*   if a method has fragment “somethingAsync(...)“, then the new stage is executed by _the default asynchronous facility_
*   if a method has fragment “somethingAsync(..., Executor)“, then the new stage is executed by the given _Executor_

Note that _the default facility_ and _the default asynchronous facility_ are specified by the _CompletionStage_ implementations, not by this interface. Looking ahead, the _CompletableFuture_ class uses the thread that completes the future (or any other threads that simultaneously are trying to do the same) as _the default facility_ and a thread pool returned by the _ForkJoinPool.commonPool()_ method as _the default asynchronous facility_.

>Note that the thread pool returned by the _ForkJoinPool.commonPool()_ method is shared across a JVM by all _CompletableFutures_ and all Parallel Streams.

The following code example demonstrates the use of the methods to pipeline computations to calculate the area of a circle. First, the pipeline takes the radius and squares it by the _thenApply_ method. Then the pipeline takes the squared radius and the constant π and multiplies them in the _thenCombine_ method. Then the area is consumed by the _thenAccept_ method that logs it and returns no result. Finally, the _thenRun_ method (which takes no argument and returns no result) logs a message that the pipeline has ended.


```
CompletableFuture<Double> pi = CompletableFuture.supplyAsync(() -> Math.PI);
CompletableFuture<Integer> radius = CompletableFuture.supplyAsync(() -> 1);

// area of a circle = π * r^2
CompletableFuture<Void> area = radius
        .thenApply(r -> r * r)
        .thenCombine(pi, (multiplier1, multiplier2) -> multiplier1 * multiplier2)
        .thenAccept(a -> logger.info("area: {}", a))
        .thenRun(() -> logger.info("operation completed"));

area.join();
```


>You should use the _thenApply_ method if you want to transform a _CompletionStage_ with a _fast_ function. You should use the _thenCompose_ method if you want to transform a _CompletionStage_ with a _slow_ function.

>You should use the _thenCompose_ method if you want to transform two _CompletionStages_ _sequentially_. You should use the _thenCombine_ method if you want to transform two _CompletionStages_ _in parallel_. 

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part1)


### Methods to handle exceptions

Each computation may complete normally or exceptionally. In asynchronous computations, the source of the exception and the recovery method can be in different threads. Therefore in this case it is not possible to use the _try-catch-finally_ statements to recover from exceptions. So the _CompletionStage_ interface has special methods to handle exceptions.

Each stage has two types of completion of equal importance: normal completion and exceptional completion. If a stage completes normally, the dependent stages start to execute normally. If a stage completes exceptionally, the dependent stages complete exceptionally, unless there is an exception recovery stage in the computation pipeline.

Summary of methods to handle exceptions:


<table>
  <tr>
   <td>When the method is called
   </td>
   <td>What the method returns
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td rowspan="2" >called on success or exception
   </td>
   <td>the same result or exception
   </td>
   <td><em>whenComplete(biConsumer)</em>
   </td>
   <td>returns a new <em>CompletionStage</em> that upon <em>normal or exceptional</em> completion consumes the result or exception of this stage and returns <em>the same result or exception without modifying them</em>
   </td>
  </tr>
  <tr>
   <td rowspan="2" >a new result
   </td>
   <td><em>handle(biFunction)</em>
   </td>
   <td>returns a new <em>CompletionStage</em> that upon <em>normal or exceptional</em> completion transforms the result or exception of this stage and returns <em>the new result</em>
   </td>
  </tr>
  <tr>
   <td>called on exception
   </td>
   <td><em>exceptionally(function)</em>
   </td>
   <td>returns a new <em>CompletionStage</em> that upon <em>exceptional</em> completion transforms the exception of this stage and returns <em>the new result</em>
   </td>
  </tr>
</table>


If you need _to perform some action_, when the previous stage completes normally or exceptionally, you should use the _whenComplete_ method. A _BiConsumer_ argument of the _whenComplete_ method is called when the previous stage completes normally or exceptionally. This method allows reading both the result (or _null_ if none) and the exception (or _null_ if none) but does not allow to change the result.

If you need _to recover from an exception_ (to replace the exception with some default value), you should use the _handle_ and _exceptionally_ methods. A _BiFunction_ argument of the _handle_ method is called when the previous stage completes normally or exceptionally. A _Function_ argument of the _exceptionally_ method is called when the previous stage completes exceptionally. In both cases, an exception is not propagated to the next stage.

The following code example demonstrates the use of the methods to handle exceptions. During the execution of stage 1 occurs an exception (division by zero). Execution of stage 2 is skipped because its previous stage is completed exceptionally. The _whenComplete_ method identifies that the previous stage completed exceptionally, but does not recover from the exception. Execution of stage 3 is also skipped because its previous stage is still completed exceptionally. The _handle_ method identifies that the previous stage completed exceptionally and replaces the exception with a default value. The execution of stage 4 is at last performed normally.


```
CompletableFuture.supplyAsync(() -> 0)
       .thenApply(i -> { logger.info("stage 1: {}", i); return 1 / i; }) // executed and failed
       .thenApply(i -> { logger.info("stage 2: {}", i); return 1 / i; }) // skipped
       .whenComplete((value, t) -> {
           if (t == null) {
               logger.info("success: {}", value);
           } else {
               logger.warn("failure: {}", t.getMessage()); // executed
           }
       })
       .thenApply(i -> { logger.info("stage 3: {}", i); return 1 / i; }) // skipped
       .handle((value, t) -> {
           if (t == null) {
               return value + 1;
           } else {
               return -1; // executed and recovered
           }
       })
       .thenApply(i -> { logger.info("stage 4: {}", i); return 1 / i; }) // executed
       .join();
```


[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part2)


## The CompletableFuture class

The _CompletableFuture_ class represents a stage in a multi-stage (possibly asynchronous) computation where stages can be _created, checked, completed_, and _read_. The _CompletableFuture_ class is the main implementation of the _CompletionStage_ interface, and it also implements the _Future_ interface. That means the _CompletableFuture_ class can simultaneously represent a _stage_ in a multi-stage computation and the _result_ of such a computation.

This class specifies _the general lifecycle_ of the future/promise implementation in the CompletableFuture API:



1. A _creating_ thread creates an incomplete future and adds computation handlers to it.
2. A _reading_ thread waits (in a blocking or non-blocking manner) until the future is completed normally or exceptionally.
3. A _completing_ thread completes the future and unblocks the _reading_ thread.

The methods of the _CompletableFuture_ class can be divided into five groups according to their purpose:



*   methods to create futures
*   methods to check futures
*   methods to complete futures
*   methods to read futures
*   methods for bulk futures operations

![methods of the CompletableFuture class](/images/methods_of_the_CompletableFuture_class.png)

The following code example demonstrates the use of the methods to handle the lifecycle of the _CompletableFuture_ class. The future is created incomplete in the first thread. Then the same thread starts checking the future for completion. After a delay, that simulates a long operation, the future is completed in the second thread. Finally, the first thread finished the checking and reads the value of the future which has already been completed.


```
ExecutorService executorService = Executors.newSingleThreadExecutor();

CompletableFuture<String> future = new CompletableFuture<>(); // creating an incomplete future

executorService.submit(() -> {
   Thread.sleep(500);
   future.complete("value"); // completing the incomplete future
   return null;
});

while (!future.isDone()) { // checking the future for completion
   Thread.sleep(1000);
}

String result = future.get(); // reading value of the completed future
logger.info("result: {}", result);

executorService.shutdown();
```



### Methods to create futures

In the most general case, a future is created incompleted in one thread and is completed in another thread. However, in some cases (for example, for testing), it may be necessary to create an already completed future.

Summary of methods to create futures:


<table>
  <tr>
   <td>Future status
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>incomplete
   </td>
   <td><em>newIncompleteFuture()</em>
   </td>
   <td>returns a new incomplete <em>CompletableFuture</em>
   </td>
  </tr>
  <tr>
   <td rowspan="2" >asynchronously completing
   </td>
   <td><em>supplyAsync(supplier)</em>
   </td>
   <td>returns a new <em>CompletableFuture</em> that is asynchronously completed after it obtains a value from the given <em>Supplier</em>
   </td>
  </tr>
  <tr>
   <td><em>runAsync(runnable)</em>
   </td>
   <td>returns a new <em>CompletableFuture</em> of that is asynchronously completed after it runs an action from the given <em>Runnable</em>
   </td>
  </tr>
  <tr>
   <td rowspan="2" >completed
   </td>
   <td><em>completedFuture(value)</em>
   </td>
   <td>returns a new <em>CompletableFuture</em> that is already completed with the given value
   </td>
  </tr>
  <tr>
   <td><em>failedFuture(throwable)</em>
   </td>
   <td>returns a new <em>CompletableFuture</em> that is already completed exceptionally with the given exception
   </td>
  </tr>
</table>


>The no-arg _CompletableFuture_ constructor also creates an incomplete future.

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part3)


### Methods to check futures

The _CompletableFuture_ class has _non-blocking_ methods for checking whether a future is incomplete, completed normally, completed exceptionally, or canceled.

Summary of the methods to check futures:


<table>
  <tr>
   <td>Behavior
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td rowspan="3" >non-blocking
   </td>
   <td><em>isDone</em>
   </td>
   <td>returns true if the <em>CompletableFuture</em> is completed in any manner: normally, exceptionally, or by cancellation
   </td>
  </tr>
  <tr>
   <td><em>isCompletedExceptionally</em>
   </td>
   <td>returns true if this <em>CompletableFuture</em> is completed exceptionally, including cancellation
   </td>
  </tr>
  <tr>
   <td><em>isCancelled</em>
   </td>
   <td>returns true if this <em>CompletableFuture</em> is canceled <em>before it completed normally</em>
   </td>
  </tr>
</table>


>It is impossible to cancel an already completed future.

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part4)


### Methods to complete futures

The _CompletableFuture_ class has methods for completing futures, which means transferring incomplete futures to one of the completed states: normal completion, exceptional completion, and cancellation.

Summary of methods to complete futures:


<table>
  <tr>
   <td>Future action
   </td>
   <td>Execution
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td rowspan="3" >complete normally
   </td>
   <td>synchronous
   </td>
   <td><em>complete(value)</em>
   </td>
   <td>completes this <em>CompletableFuture</em> with the given value if not already completed
   </td>
  </tr>
  <tr>
   <td rowspan="3" >asynchronous
   </td>
   <td><em>completeAsync(supplier)</em>
   </td>
   <td>completes this <em>CompletableFuture</em> with the result of the given <em>Supplier</em>
   </td>
  </tr>
  <tr>
   <td><em>completeOnTimeout(value, timeout, timeUnit)</em>
   </td>
   <td>completes this <em>CompletableFuture</em> with the given value if not already completed before the given timeout
   </td>
  </tr>
  <tr>
   <td>complete normally or exceptionally depends on timeout
   </td>
   <td><em>orTimeout(timeout, timeUnit)</em>
   </td>
   <td>exceptionally completes this <em>CompletableFuture</em> with a <em>TimeoutException</em> if not already completed before the given timeout
   </td>
  </tr>
  <tr>
   <td rowspan="2" >complete exceptionally
   </td>
   <td rowspan="2" >synchronous
   </td>
   <td><em>completeExceptionally(throwable)</em>
   </td>
   <td>completes this <em>CompletableFuture</em> with the given exception if not already completed
   </td>
  </tr>
  <tr>
   <td><em>cancel(mayInterruptIfRunning)</em>
   </td>
   <td>completes this <em>CompletableFuture</em> with a <em>CancellationException</em>, if not already completed 
   </td>
  </tr>
</table>


The _cancel(boolean mayInterruptIfRunning)_ method has implementation specifics in the _CompletableFuture_ class. The parameter _mayInterruptIfRunning_ does not affect because thread interrupts are not used here to control processing. When the _cancel_ method is called, the computation is canceled with the _CancellationException_, but the _Thread.interrupt()_ method is not called to interrupt the underlying thread.

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part5)


### Methods to read futures

The _CompletableFuture_ class has methods for reading futures, waiting if necessary. Note that in most cases, these methods should be used as the final step in a computation pipeline.

Summary of methods to read futures:


<table>
  <tr>
   <td>Behavior
   </td>
   <td>Thrown exceptions
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td rowspan="2" >blocking
   </td>
   <td>throws checked and unchecked exceptions
   </td>
   <td><em>get()</em>
   </td>
   <td>returns the result value when complete (waits if necessary) or throws an exception if completed exceptionally 
   </td>
  </tr>
  <tr>
   <td>throws <em>only</em> unchecked exceptions 
   </td>
   <td><em>join()</em>
   </td>
   <td>returns the result value when complete (waits if necessary) or throws an <em>unchecked</em> if completed exceptionally 
   </td>
  </tr>
  <tr>
   <td>time-blocking
   </td>
   <td>throws checked and unchecked exceptions
   </td>
   <td><em>get(timeout, timeUnit)</em>
   </td>
   <td>returns the result value when complete (waits for at most the given time) or throws an exception if completed exceptionally
   </td>
  </tr>
  <tr>
   <td>non-blocking
   </td>
   <td>throws <em>only</em> unchecked exceptions 
   </td>
   <td><em>getNow(valueIfAbsent)</em>
   </td>
   <td>returns the result value (or throws an <em>unchecked</em> exception if completed exceptionally) if completed, else returns the given <em>valueIfAbsent</em>
   </td>
  </tr>
</table>


The _get()_ and _get(timeout, timeUnit)_ methods can throw checked exceptions: _ExecutionException_ (if the future is completed exceptionally) and _InterruptedException_ (if the current thread is interrupted). Also, the time-bounded _get(timeout, timeUnit)_ method can throw checked _TimeoutException_ (if the timeout occurs).

The _join_ and _getNow_ methods can only throw unchecked _CompletionException_ (if the future is completed exceptionally).

All of these methods can also throw unchecked _CancellationException_ (if the computation is canceled).

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part6)


### Methods for bulk future operations

The _CompletionStage_ interface has methods to wait for all (_thenCombine_, _thenAcceptBoth_, _runAfterBoth_) and any (_applyToEither_, _acceptEitherrun_, _runAfterEither_) of _two_ computations to complete. The _CompletableFuture_ class extends this functionality and has two static methods to wait for all or any of _many_ futures to complete.

Summary of methods for bulk futures operations:


<table>
  <tr>
   <td>Similarity
   </td>
   <td>Method
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>similar to <em>runAfterBoth</em>
   </td>
   <td><em>allOf(completableFuture..)</em>
   </td>
   <td>returns a new <em>CompletableFuture&lt;Void></em> that is completed when <em>all</em> of the given <em>CompletableFuture</em>s complete
   </td>
  </tr>
  <tr>
   <td>similar to <em>applyToEither</em>
   </td>
   <td><em>anyOf(completableFuture..)</em>
   </td>
   <td>returns a new <em>CompletableFuture&lt;Object></em> that is completed when <em>any</em> of the given <em>CompletableFuture</em>s complete, with the same result
   </td>
  </tr>
</table>


>Note that all input futures can be of different generic types - the methods have variable arguments of type _CompletableFuture&lt;?>_.

[code examples](https://github.com/aliakh/demo-java-completablefuture/tree/main/src/test/java/demo/completable_future/part7)


## Conclusion

The CompletableFuture API is a high-level API that allows you to develop _fluent_ asynchronous code. This API is not simple, but it is worth learning if you want to write _efficient_ asynchronous code.

There are the following rules of thumb for using CompletableFuture API:



*   Know which threads execute which stages, do not allow high-priority threads to execute long-running low-priority tasks
*   Avoid blocking methods _inside_ a computation pipeline
*   Avoid short (hundreds of milliseconds) _asynchronous_ computations because frequent context switching can introduce significant overhead
*   Be aware of the new exception handling mechanism that works differently than the _try-catch-finally_ statements
*   Manage timeouts not to wait too long (perhaps indefinitely) for a stuck computation

The CompletableFuture API is quite complex and justifiable to use when a single result depends on many stages that form a rather complicated _directed acyclic graph_. It is wise to try simpler asynchronous APIs first, for example, Parallel Streams or _ExecutorServices_. Be aware of the disadvantages of asynchronous programming - asynchronous code is often much more difficult to implement, understand, and debug. Make sure that the CompletableFuture API is the right tool for your job. 

Complete code examples are available in the [GitHub repository](https://github.com/aliakh/demo-java-completablefuture).
