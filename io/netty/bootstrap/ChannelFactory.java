package io.netty.bootstrap;

public interface ChannelFactory<T extends io.netty.channel.Channel> {
  T newChannel();
}
