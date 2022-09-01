package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.SoundRewriter;

public class SoundRewriter extends SoundRewriter {
  private final BackwardsProtocol protocol;
  
  public SoundRewriter(BackwardsProtocol protocol) {
    super((Protocol)protocol);
    this.protocol = protocol;
  }
  
  public void registerNamedSound(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(SoundRewriter.this.getNamedSoundHandler());
          }
        });
  }
  
  public void registerStopSound(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(SoundRewriter.this.getStopSoundHandler());
          }
        });
  }
  
  public PacketHandler getNamedSoundHandler() {
    return wrapper -> {
        String soundId = (String)wrapper.get(Type.STRING, 0);
        String mappedId = this.protocol.getMappingData().getMappedNamedSound(soundId);
        if (mappedId == null)
          return; 
        if (!mappedId.isEmpty()) {
          wrapper.set(Type.STRING, 0, mappedId);
        } else {
          wrapper.cancel();
        } 
      };
  }
  
  public PacketHandler getStopSoundHandler() {
    return wrapper -> {
        byte flags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
        if ((flags & 0x2) == 0)
          return; 
        if ((flags & 0x1) != 0)
          wrapper.passthrough((Type)Type.VAR_INT); 
        String soundId = (String)wrapper.read(Type.STRING);
        String mappedId = this.protocol.getMappingData().getMappedNamedSound(soundId);
        if (mappedId == null) {
          wrapper.write(Type.STRING, soundId);
          return;
        } 
        if (!mappedId.isEmpty()) {
          wrapper.write(Type.STRING, mappedId);
        } else {
          wrapper.cancel();
        } 
      };
  }
}
