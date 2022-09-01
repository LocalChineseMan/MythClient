package org.apache.logging.log4j.core.async;

import java.util.concurrent.BlockingQueue;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "JCToolsBlockingQueue", category = "Core", elementType = "BlockingQueueFactory")
public class JCToolsBlockingQueueFactory<E> implements BlockingQueueFactory<E> {
  private final WaitStrategy waitStrategy;
  
  private JCToolsBlockingQueueFactory(WaitStrategy waitStrategy) {
    this.waitStrategy = waitStrategy;
  }
  
  public BlockingQueue<E> create(int capacity) {
    return (BlockingQueue<E>)new MpscBlockingQueue(capacity, this.waitStrategy);
  }
  
  @PluginFactory
  public static <E> JCToolsBlockingQueueFactory<E> createFactory(@PluginAttribute(value = "WaitStrategy", defaultString = "PARK") WaitStrategy waitStrategy) {
    return new JCToolsBlockingQueueFactory(waitStrategy);
  }
  
  private static interface JCToolsBlockingQueueFactory {}
  
  public enum JCToolsBlockingQueueFactory {
  
  }
  
  private static final class JCToolsBlockingQueueFactory {}
}
