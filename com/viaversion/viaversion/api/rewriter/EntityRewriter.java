package com.viaversion.viaversion.api.rewriter;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import java.util.List;

public interface EntityRewriter<T extends com.viaversion.viaversion.api.protocol.Protocol> extends Rewriter<T> {
  EntityType typeFromId(int paramInt);
  
  default EntityType objectTypeFromId(int type) {
    return typeFromId(type);
  }
  
  int newEntityId(int paramInt);
  
  void handleMetadata(int paramInt, List<Metadata> paramList, UserConnection paramUserConnection);
  
  default <E extends com.viaversion.viaversion.api.data.entity.EntityTracker> E tracker(UserConnection connection) {
    return (E)connection.getEntityTracker(protocol().getClass());
  }
}
