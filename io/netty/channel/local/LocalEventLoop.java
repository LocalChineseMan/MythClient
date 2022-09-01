package io.netty.channel.local;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import java.util.concurrent.ThreadFactory;

final class LocalEventLoop extends SingleThreadEventLoop {
  LocalEventLoop(LocalEventLoopGroup parent, ThreadFactory threadFactory) {
    super((EventLoopGroup)parent, threadFactory, true);
  }
  
  protected void run() {
    do {
      Runnable task = takeTask();
      if (task == null)
        continue; 
      task.run();
      updateLastExecutionTime();
    } while (!confirmShutdown());
  }
}
