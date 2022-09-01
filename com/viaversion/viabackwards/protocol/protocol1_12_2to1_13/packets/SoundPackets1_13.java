package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.NamedSoundMapping;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class SoundPackets1_13 extends RewriterBase<Protocol1_12_2To1_13> {
  private static final String[] SOUND_SOURCES = new String[] { "master", "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice" };
  
  public SoundPackets1_13(Protocol1_12_2To1_13 protocol) {
    super((Protocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(wrapper -> {
                  String newSound = (String)wrapper.get(Type.STRING, 0);
                  String oldSound = NamedSoundMapping.getOldId(newSound);
                  if (oldSound != null || (oldSound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getMappedNamedSound(newSound)) != null) {
                    wrapper.set(Type.STRING, 0, oldSound);
                  } else if (!Via.getConfig().isSuppressConversionWarnings()) {
                    ViaBackwards.getPlatform().getLogger().warning("Unknown named sound in 1.13->1.12 protocol: " + newSound);
                  } 
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.STOP_SOUND, (ClientboundPacketType)ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String source;
                  String sound;
                  wrapper.write(Type.STRING, "MC|StopSound");
                  byte flags = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                  if ((flags & 0x1) != 0) {
                    source = SoundPackets1_13.SOUND_SOURCES[((Integer)wrapper.read((Type)Type.VAR_INT)).intValue()];
                  } else {
                    source = "";
                  } 
                  if ((flags & 0x2) != 0) {
                    String newSound = (String)wrapper.read(Type.STRING);
                    sound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getMappedNamedSound(newSound);
                    if (sound == null)
                      sound = ""; 
                  } else {
                    sound = "";
                  } 
                  wrapper.write(Type.STRING, source);
                  wrapper.write(Type.STRING, sound);
                });
          }
        });
    ((Protocol1_12_2To1_13)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SOUND, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int newSound = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int oldSound = ((Protocol1_12_2To1_13)SoundPackets1_13.this.protocol).getMappingData().getSoundMappings().getNewId(newSound);
                  if (oldSound == -1) {
                    wrapper.cancel();
                  } else {
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(oldSound));
                  } 
                });
          }
        });
  }
}
