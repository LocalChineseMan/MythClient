package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import java.util.List;

public abstract class MetaListTypeTemplate extends Type<List<Metadata>> {
  protected MetaListTypeTemplate() {
    super("MetaData List", List.class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)MetaListTypeTemplate.class;
  }
}
