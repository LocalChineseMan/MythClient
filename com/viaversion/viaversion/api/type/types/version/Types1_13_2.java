package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.api.type.types.minecraft.Particle1_13_2Type;
import java.util.List;

public class Types1_13_2 {
  public static final Type<Metadata> METADATA = (Type<Metadata>)new Metadata1_13_2Type();
  
  public static final Type<List<Metadata>> METADATA_LIST = (Type<List<Metadata>>)new MetaListType(METADATA);
  
  public static final Type<Particle> PARTICLE = (Type<Particle>)new Particle1_13_2Type();
}
