package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import java.util.List;

public class Types1_8 {
  public static final Type<Metadata> METADATA = (Type<Metadata>)new Metadata1_8Type();
  
  public static final Type<List<Metadata>> METADATA_LIST = (Type<List<Metadata>>)new MetadataList1_8Type();
  
  public static final Type<ChunkSection> CHUNK_SECTION = new ChunkSectionType1_8();
}
