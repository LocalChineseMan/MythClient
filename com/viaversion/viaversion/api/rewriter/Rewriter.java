package com.viaversion.viaversion.api.rewriter;

public interface Rewriter<T extends com.viaversion.viaversion.api.protocol.Protocol> {
  void register();
  
  T protocol();
}
