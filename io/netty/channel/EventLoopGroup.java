package io.netty.channel;

import io.netty.util.concurrent.EventExecutorGroup;

public interface EventLoopGroup extends EventExecutorGroup {
  EventLoop next();
  
  ChannelFuture register(Channel paramChannel);
  
  ChannelFuture register(Channel paramChannel, ChannelPromise paramChannelPromise);
}
