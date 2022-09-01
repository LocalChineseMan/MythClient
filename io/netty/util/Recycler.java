package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler<T> {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
  
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(-2147483648);
  
  private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
  
  private static final int DEFAULT_MAX_CAPACITY;
  
  static {
    int maxCapacity = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity.default", 0);
    if (maxCapacity <= 0)
      maxCapacity = 262144; 
    DEFAULT_MAX_CAPACITY = maxCapacity;
    if (logger.isDebugEnabled())
      logger.debug("-Dio.netty.recycler.maxCapacity.default: {}", Integer.valueOf(DEFAULT_MAX_CAPACITY)); 
  }
  
  private static final int INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY, 256);
  
  private final int maxCapacity;
  
  private final FastThreadLocal<Stack<T>> threadLocal = new FastThreadLocal<Stack<T>>() {
      protected Recycler.Stack<T> initialValue() {
        return new Recycler.Stack<T>(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacity);
      }
    };
  
  protected Recycler() {
    this(DEFAULT_MAX_CAPACITY);
  }
  
  protected Recycler(int maxCapacity) {
    this.maxCapacity = Math.max(0, maxCapacity);
  }
  
  public final T get() {
    Stack<T> stack = (Stack<T>)this.threadLocal.get();
    DefaultHandle handle = stack.pop();
    if (handle == null) {
      handle = stack.newHandle();
      handle.value = newObject(handle);
    } 
    return (T)handle.value;
  }
  
  public final boolean recycle(T o, Handle handle) {
    DefaultHandle h = (DefaultHandle)handle;
    if (h.stack.parent != this)
      return false; 
    if (o != h.value)
      throw new IllegalArgumentException("o does not belong to handle"); 
    h.recycle();
    return true;
  }
  
  static final class DefaultHandle implements Handle {
    private int lastRecycledId;
    
    private int recycleId;
    
    private Recycler.Stack<?> stack;
    
    private Object value;
    
    DefaultHandle(Recycler.Stack<?> stack) {
      this.stack = stack;
    }
    
    public void recycle() {
      Thread thread = Thread.currentThread();
      if (thread == this.stack.thread) {
        this.stack.push(this);
        return;
      } 
      Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> delayedRecycled = (Map<Recycler.Stack<?>, Recycler.WeakOrderQueue>)Recycler.DELAYED_RECYCLED.get();
      Recycler.WeakOrderQueue queue = delayedRecycled.get(this.stack);
      if (queue == null)
        delayedRecycled.put(this.stack, queue = new Recycler.WeakOrderQueue(this.stack, thread)); 
      queue.add(this);
    }
  }
  
  private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED = new FastThreadLocal<Map<Stack<?>, WeakOrderQueue>>() {
      protected Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> initialValue() {
        return new WeakHashMap<Recycler.Stack<?>, Recycler.WeakOrderQueue>();
      }
    };
  
  protected abstract T newObject(Handle paramHandle);
  
  static final class Stack<T> {
    final Recycler<T> parent;
    
    final Thread thread;
    
    private Recycler.DefaultHandle[] elements;
    
    private final int maxCapacity;
    
    private int size;
    
    private volatile Recycler.WeakOrderQueue head;
    
    private Recycler.WeakOrderQueue cursor;
    
    private Recycler.WeakOrderQueue prev;
    
    Stack(Recycler<T> parent, Thread thread, int maxCapacity) {
      this.parent = parent;
      this.thread = thread;
      this.maxCapacity = maxCapacity;
      this.elements = new Recycler.DefaultHandle[Recycler.INITIAL_CAPACITY];
    }
    
    Recycler.DefaultHandle pop() {
      int size = this.size;
      if (size == 0) {
        if (!scavenge())
          return null; 
        size = this.size;
      } 
      size--;
      Recycler.DefaultHandle ret = this.elements[size];
      if (ret.lastRecycledId != ret.recycleId)
        throw new IllegalStateException("recycled multiple times"); 
      ret.recycleId = 0;
      ret.lastRecycledId = 0;
      this.size = size;
      return ret;
    }
    
    boolean scavenge() {
      if (scavengeSome())
        return true; 
      this.prev = null;
      this.cursor = this.head;
      return false;
    }
    
    boolean scavengeSome() {
      boolean success = false;
      Recycler.WeakOrderQueue cursor = this.cursor, prev = this.prev;
      while (cursor != null) {
        if (cursor.transfer(this)) {
          success = true;
          break;
        } 
        Recycler.WeakOrderQueue next = Recycler.WeakOrderQueue.access$1500(cursor);
        if (Recycler.WeakOrderQueue.access$1600(cursor).get() == null) {
          if (cursor.hasFinalData())
            do {
            
            } while (cursor.transfer(this)); 
          if (prev != null)
            Recycler.WeakOrderQueue.access$1502(prev, next); 
        } else {
          prev = cursor;
        } 
        cursor = next;
      } 
      this.prev = prev;
      this.cursor = cursor;
      return success;
    }
    
    void push(Recycler.DefaultHandle item) {
      if ((item.recycleId | item.lastRecycledId) != 0)
        throw new IllegalStateException("recycled already"); 
      item.recycleId = item.lastRecycledId = Recycler.OWN_THREAD_ID;
      int size = this.size;
      if (size == this.elements.length) {
        if (size == this.maxCapacity)
          return; 
        this.elements = Arrays.<Recycler.DefaultHandle>copyOf(this.elements, size << 1);
      } 
      this.elements[size] = item;
      this.size = size + 1;
    }
    
    Recycler.DefaultHandle newHandle() {
      return new Recycler.DefaultHandle(this);
    }
  }
  
  private static final class Recycler {}
  
  public static interface Handle {}
}
