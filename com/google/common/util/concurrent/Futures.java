package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class Futures {
  public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<Exception, X> mapper) {
    return (CheckedFuture<V, X>)new MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(future), mapper);
  }
  
  private static abstract class ImmediateFuture<V> implements ListenableFuture<V> {
    private ImmediateFuture() {}
    
    private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
    
    public void addListener(Runnable listener, Executor executor) {
      Preconditions.checkNotNull(listener, "Runnable was null.");
      Preconditions.checkNotNull(executor, "Executor was null.");
      try {
        executor.execute(listener);
      } catch (RuntimeException e) {
        log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, e);
      } 
    }
    
    public boolean cancel(boolean mayInterruptIfRunning) {
      return false;
    }
    
    public V get(long timeout, TimeUnit unit) throws ExecutionException {
      Preconditions.checkNotNull(unit);
      return get();
    }
    
    public boolean isCancelled() {
      return false;
    }
    
    public boolean isDone() {
      return true;
    }
    
    public abstract V get() throws ExecutionException;
  }
  
  private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
    @Nullable
    private final V value;
    
    ImmediateSuccessfulFuture(@Nullable V value) {
      this.value = value;
    }
    
    public V get() {
      return this.value;
    }
  }
  
  public static <V> ListenableFuture<V> immediateFuture(@Nullable V value) {
    return new ImmediateSuccessfulFuture<V>(value);
  }
  
  public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V value) {
    return (CheckedFuture<V, X>)new ImmediateSuccessfulCheckedFuture(value);
  }
  
  public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    return (ListenableFuture<V>)new ImmediateFailedFuture(throwable);
  }
  
  public static <V> ListenableFuture<V> immediateCancelledFuture() {
    return (ListenableFuture<V>)new ImmediateCancelledFuture();
  }
  
  public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X exception) {
    Preconditions.checkNotNull(exception);
    return (CheckedFuture<V, X>)new ImmediateFailedCheckedFuture((Exception)exception);
  }
  
  public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> input, FutureFallback<? extends V> fallback) {
    return withFallback(input, fallback, MoreExecutors.sameThreadExecutor());
  }
  
  public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> input, FutureFallback<? extends V> fallback, Executor executor) {
    Preconditions.checkNotNull(fallback);
    return (ListenableFuture<V>)new FallbackFuture(input, fallback, executor);
  }
  
  public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function) {
    return transform(input, function, MoreExecutors.sameThreadExecutor());
  }
  
  public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
    ChainingListenableFuture<I, O> output = new ChainingListenableFuture(function, input, null);
    input.addListener((Runnable)output, executor);
    return (ListenableFuture)output;
  }
  
  public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
    return transform(input, function, MoreExecutors.sameThreadExecutor());
  }
  
  public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, final Function<? super I, ? extends O> function, Executor executor) {
    Preconditions.checkNotNull(function);
    AsyncFunction<I, O> wrapperFunction = new AsyncFunction<I, O>() {
        public ListenableFuture<O> apply(I input) {
          O output = (O)function.apply(input);
          return Futures.immediateFuture(output);
        }
      };
    return transform(input, wrapperFunction, executor);
  }
  
  public static <I, O> Future<O> lazyTransform(Future<I> input, Function<? super I, ? extends O> function) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(function);
    return (Future<O>)new Object(input, function);
  }
  
  public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> nested) {
    return transform(nested, (AsyncFunction)DEREFERENCER);
  }
  
  private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() {
      public ListenableFuture<Object> apply(ListenableFuture<Object> input) {
        return input;
      }
    };
  
  @Beta
  public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... futures) {
    return listFuture(ImmutableList.copyOf((Object[])futures), true, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
  }
  
  public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> future) {
    return (ListenableFuture<V>)new NonCancellationPropagatingFuture(future);
  }
  
  @Beta
  public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... futures) {
    return listFuture(ImmutableList.copyOf((Object[])futures), false, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> futures) {
    ConcurrentLinkedQueue<AsyncSettableFuture<T>> delegates = Queues.newConcurrentLinkedQueue();
    ImmutableList.Builder<ListenableFuture<T>> listBuilder = ImmutableList.builder();
    SerializingExecutor executor = new SerializingExecutor(MoreExecutors.sameThreadExecutor());
    for (ListenableFuture<? extends T> future : futures) {
      AsyncSettableFuture<T> delegate = AsyncSettableFuture.create();
      delegates.add(delegate);
      future.addListener((Runnable)new Object(delegates, future), (Executor)executor);
      listBuilder.add(delegate);
    } 
    return listBuilder.build();
  }
  
  public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback) {
    addCallback(future, callback, MoreExecutors.sameThreadExecutor());
  }
  
  public static <V> void addCallback(final ListenableFuture<V> future, final FutureCallback<? super V> callback, Executor executor) {
    Preconditions.checkNotNull(callback);
    Runnable callbackListener = new Runnable() {
        public void run() {
          V value;
          try {
            value = Uninterruptibles.getUninterruptibly(future);
          } catch (ExecutionException e) {
            callback.onFailure(e.getCause());
            return;
          } catch (RuntimeException e) {
            callback.onFailure(e);
            return;
          } catch (Error e) {
            callback.onFailure(e);
            return;
          } 
          callback.onSuccess(value);
        }
      };
    future.addListener(callbackListener, executor);
  }
  
  public static <V, X extends Exception> V get(Future<V> future, Class<X> exceptionClass) throws X {
    Preconditions.checkNotNull(future);
    Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
    try {
      return future.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw (X)new AssertionError();
    } 
  }
  
  public static <V, X extends Exception> V get(Future<V> future, long timeout, TimeUnit unit, Class<X> exceptionClass) throws X {
    Preconditions.checkNotNull(future);
    Preconditions.checkNotNull(unit);
    Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { exceptionClass });
    try {
      return future.get(timeout, unit);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (TimeoutException e) {
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw (X)new AssertionError();
    } 
  }
  
  private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws X {
    if (cause instanceof Error)
      throw (X)new ExecutionError((Error)cause); 
    if (cause instanceof RuntimeException)
      throw (X)new UncheckedExecutionException(cause); 
    throw newWithCause(exceptionClass, cause);
  }
  
  public static <V> V getUnchecked(Future<V> future) {
    Preconditions.checkNotNull(future);
    try {
      return Uninterruptibles.getUninterruptibly(future);
    } catch (ExecutionException e) {
      wrapAndThrowUnchecked(e.getCause());
      throw new AssertionError();
    } 
  }
  
  private static void wrapAndThrowUnchecked(Throwable cause) {
    if (cause instanceof Error)
      throw new ExecutionError((Error)cause); 
    throw new UncheckedExecutionException(cause);
  }
  
  private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause) {
    List<Constructor<X>> constructors = (List)Arrays.asList(exceptionClass.getConstructors());
    for (Constructor<X> constructor : preferringStrings(constructors)) {
      Exception exception = newFromConstructor(constructor, cause);
      if (exception != null) {
        if (exception.getCause() == null)
          exception.initCause(cause); 
        return (X)exception;
      } 
    } 
    throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
  }
  
  private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors) {
    return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
  }
  
  private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function<Constructor<?>, Boolean>() {
        public Boolean apply(Constructor<?> input) {
          return Boolean.valueOf(Arrays.<Class<?>>asList(input.getParameterTypes()).contains(String.class));
        }
      }).reverse();
  
  @Nullable
  private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause) {
    Class<?>[] paramTypes = constructor.getParameterTypes();
    Object[] params = new Object[paramTypes.length];
    for (int i = 0; i < paramTypes.length; i++) {
      Class<?> paramType = paramTypes[i];
      if (paramType.equals(String.class)) {
        params[i] = cause.toString();
      } else if (paramType.equals(Throwable.class)) {
        params[i] = cause;
      } else {
        return null;
      } 
    } 
    try {
      return constructor.newInstance(params);
    } catch (IllegalArgumentException e) {
      return null;
    } catch (InstantiationException e) {
      return null;
    } catch (IllegalAccessException e) {
      return null;
    } catch (InvocationTargetException e) {
      return null;
    } 
  }
  
  private static class CombinedFuture<V, C> extends AbstractFuture<C> {
    private static final Logger logger = Logger.getLogger(CombinedFuture.class.getName());
    
    ImmutableCollection<? extends ListenableFuture<? extends V>> futures;
    
    final boolean allMustSucceed;
    
    final AtomicInteger remaining;
    
    Futures.FutureCombiner<V, C> combiner;
    
    List<Optional<V>> values;
    
    final Object seenExceptionsLock = new Object();
    
    Set<Throwable> seenExceptions;
    
    CombinedFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor, Futures.FutureCombiner<V, C> combiner) {
      this.futures = futures;
      this.allMustSucceed = allMustSucceed;
      this.remaining = new AtomicInteger(futures.size());
      this.combiner = combiner;
      this.values = Lists.newArrayListWithCapacity(futures.size());
      init(listenerExecutor);
    }
    
    protected void init(Executor listenerExecutor) {
      addListener(new Runnable() {
            public void run() {
              if (Futures.CombinedFuture.this.isCancelled())
                for (ListenableFuture<?> future : (Iterable<ListenableFuture<?>>)Futures.CombinedFuture.this.futures)
                  future.cancel(Futures.CombinedFuture.this.wasInterrupted());  
              Futures.CombinedFuture.this.futures = null;
              Futures.CombinedFuture.this.values = null;
              Futures.CombinedFuture.this.combiner = null;
            }
          },  MoreExecutors.sameThreadExecutor());
      if (this.futures.isEmpty()) {
        set(this.combiner.combine((List<Optional<V>>)ImmutableList.of()));
        return;
      } 
      int i;
      for (i = 0; i < this.futures.size(); i++)
        this.values.add(null); 
      i = 0;
      for (ListenableFuture<? extends V> listenable : this.futures) {
        final int index = i++;
        listenable.addListener(new Runnable() {
              public void run() {
                Futures.CombinedFuture.this.setOneValue(index, listenable);
              }
            }listenerExecutor);
      } 
    }
    
    private void setExceptionAndMaybeLog(Throwable throwable) {
      boolean visibleFromOutputFuture = false;
      boolean firstTimeSeeingThisException = true;
      if (this.allMustSucceed) {
        visibleFromOutputFuture = setException(throwable);
        synchronized (this.seenExceptionsLock) {
          if (this.seenExceptions == null)
            this.seenExceptions = Sets.newHashSet(); 
          firstTimeSeeingThisException = this.seenExceptions.add(throwable);
        } 
      } 
      if (throwable instanceof Error || (this.allMustSucceed && !visibleFromOutputFuture && firstTimeSeeingThisException))
        logger.log(Level.SEVERE, "input future failed.", throwable); 
    }
    
    private void setOneValue(int index, Future<? extends V> future) {
      List<Optional<V>> localValues = this.values;
      if (isDone() || localValues == null)
        Preconditions.checkState((this.allMustSucceed || isCancelled()), "Future was done before all dependencies completed"); 
      try {
        Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
        V returnValue = Uninterruptibles.getUninterruptibly((Future)future);
        if (localValues != null)
          localValues.set(index, Optional.fromNullable(returnValue)); 
      } catch (CancellationException e) {
        if (this.allMustSucceed)
          cancel(false); 
      } catch (ExecutionException e) {
        setExceptionAndMaybeLog(e.getCause());
      } catch (Throwable t) {
        setExceptionAndMaybeLog(t);
      } finally {
        int newRemaining = this.remaining.decrementAndGet();
        Preconditions.checkState((newRemaining >= 0), "Less than 0 remaining futures");
        if (newRemaining == 0) {
          Futures.FutureCombiner<V, C> localCombiner = this.combiner;
          if (localCombiner != null && localValues != null) {
            set(localCombiner.combine(localValues));
          } else {
            Preconditions.checkState(isDone());
          } 
        } 
      } 
    }
  }
  
  private static <V> ListenableFuture<List<V>> listFuture(ImmutableList<ListenableFuture<? extends V>> futures, boolean allMustSucceed, Executor listenerExecutor) {
    return new CombinedFuture<V, List<V>>((ImmutableCollection<? extends ListenableFuture<? extends V>>)futures, allMustSucceed, listenerExecutor, new FutureCombiner<V, List<V>>() {
          public List<V> combine(List<Optional<V>> values) {
            List<V> result = Lists.newArrayList();
            for (Optional<V> element : values)
              result.add((element != null) ? (V)element.orNull() : null); 
            return Collections.unmodifiableList(result);
          }
        });
  }
  
  private static class Futures {}
  
  private static interface FutureCombiner<V, C> {
    C combine(List<Optional<V>> param1List);
  }
  
  private static class Futures {}
  
  private static class Futures {}
  
  private static class Futures {}
  
  private static class Futures {}
  
  private static class Futures {}
  
  private static class Futures {}
  
  private static class Futures {}
}
