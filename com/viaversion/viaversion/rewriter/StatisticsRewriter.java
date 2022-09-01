package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;

public class StatisticsRewriter {
  private final Protocol protocol;
  
  private final int customStatsCategory = 8;
  
  public StatisticsRewriter(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public void register(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  int newSize = size;
                  for (int i = 0; i < size; i++) {
                    int categoryId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int statisticId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int value = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (categoryId == 8 && StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings() != null) {
                      statisticId = StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings().getNewId(statisticId);
                      if (statisticId == -1) {
                        newSize--;
                        continue;
                      } 
                    } else {
                      RegistryType type = StatisticsRewriter.this.getRegistryTypeForStatistic(categoryId);
                      IdRewriteFunction statisticsRewriter;
                      if (type != null && (statisticsRewriter = StatisticsRewriter.this.getRewriter(type)) != null)
                        statisticId = statisticsRewriter.rewrite(statisticId); 
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(categoryId));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(statisticId));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(value));
                    continue;
                  } 
                  if (newSize != size)
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(newSize)); 
                });
          }
        });
  }
  
  protected IdRewriteFunction getRewriter(RegistryType type) {
    switch (null.$SwitchMap$com$viaversion$viaversion$api$minecraft$RegistryType[type.ordinal()]) {
      case 1:
        return (this.protocol.getMappingData().getBlockMappings() != null) ? (id -> this.protocol.getMappingData().getNewBlockId(id)) : null;
      case 2:
        return (this.protocol.getMappingData().getItemMappings() != null) ? (id -> this.protocol.getMappingData().getNewItemId(id)) : null;
      case 3:
        return (this.protocol.getEntityRewriter() != null) ? (id -> this.protocol.getEntityRewriter().newEntityId(id)) : null;
    } 
    throw new IllegalArgumentException("Unknown registry type in statistics packet: " + type);
  }
  
  public RegistryType getRegistryTypeForStatistic(int statisticsId) {
    switch (statisticsId) {
      case 0:
        return RegistryType.BLOCK;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        return RegistryType.ITEM;
      case 6:
      case 7:
        return RegistryType.ENTITY;
    } 
    return null;
  }
}
