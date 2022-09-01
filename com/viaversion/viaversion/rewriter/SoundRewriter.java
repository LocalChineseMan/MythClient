package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;

public class SoundRewriter {
  protected final Protocol protocol;
  
  protected final IdRewriteFunction idRewriter;
  
  public SoundRewriter(Protocol protocol) {
    this.protocol = protocol;
    this.idRewriter = (id -> protocol.getMappingData().getSoundMappings().getNewId(id));
  }
  
  public SoundRewriter(Protocol protocol, IdRewriteFunction idRewriter) {
    this.protocol = protocol;
    this.idRewriter = idRewriter;
  }
  
  public void registerSound(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(SoundRewriter.this.getSoundHandler());
          }
        });
  }
  
  public PacketHandler getSoundHandler() {
    return wrapper -> {
        int soundId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        int mappedId = this.idRewriter.rewrite(soundId);
        if (mappedId == -1) {
          wrapper.cancel();
        } else if (soundId != mappedId) {
          wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(mappedId));
        } 
      };
  }
}
