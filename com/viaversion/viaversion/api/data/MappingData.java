package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.util.List;

public interface MappingData {
  void load();
  
  int getNewBlockStateId(int paramInt);
  
  int getNewBlockId(int paramInt);
  
  int getNewItemId(int paramInt);
  
  int getOldItemId(int paramInt);
  
  int getNewParticleId(int paramInt);
  
  List<TagData> getTags(RegistryType paramRegistryType);
  
  Int2IntBiMap getItemMappings();
  
  ParticleMappings getParticleMappings();
  
  Mappings getBlockMappings();
  
  Mappings getBlockStateMappings();
  
  Mappings getSoundMappings();
  
  Mappings getStatisticsMappings();
}
