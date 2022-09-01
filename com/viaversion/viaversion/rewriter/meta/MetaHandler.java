package com.viaversion.viaversion.rewriter.meta;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;

@FunctionalInterface
public interface MetaHandler {
  void handle(MetaHandlerEvent paramMetaHandlerEvent, Metadata paramMetadata);
}
