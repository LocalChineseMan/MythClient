package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;

public class PlayerPackets1_14 extends RewriterBase<Protocol1_13_2To1_14> {
  public PlayerPackets1_14(Protocol1_13_2To1_14 protocol) {
    super((Protocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.SERVER_DIFFICULTY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.BOOLEAN, (Type)Type.NOTHING);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_14.OPEN_SIGN_EDITOR, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.QUERY_BLOCK_NBT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.PLAYER_DIGGING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.BYTE);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (type == 0) {
                      wrapper.passthrough(Type.STRING);
                    } else if (type == 1) {
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                      wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    } 
                  }
                });
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.UPDATE_COMMAND_BLOCK, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.UPDATE_STRUCTURE_BLOCK, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    ((Protocol1_13_2To1_14)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_13.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.read(Type.POSITION);
                    int face = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    float x = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    float y = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    float z = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(hand));
                    wrapper.write(Type.POSITION1_14, position);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(face));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(x));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(y));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(z));
                    wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  }
                });
          }
        });
  }
}
