package com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;

public class Protocol1_13_2To1_13_1 extends AbstractProtocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {
  public Protocol1_13_2To1_13_1() {
    super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
  }
  
  protected void registerPackets() {
    InventoryPackets.register((Protocol)this);
    WorldPackets.register((Protocol)this);
    EntityPackets.register((Protocol)this);
    registerServerbound((ServerboundPacketType)ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            map(Type.FLAT_VAR_INT_ITEM, Type.FLAT_ITEM);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_13.ADVANCEMENTS, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.passthrough((Type)Type.BOOLEAN);
                    int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int i = 0; i < size; i++) {
                      wrapper.passthrough(Type.STRING);
                      if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                        wrapper.passthrough(Type.STRING); 
                      if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue()) {
                        wrapper.passthrough(Type.COMPONENT);
                        wrapper.passthrough(Type.COMPONENT);
                        Item icon = (Item)wrapper.read(Type.FLAT_ITEM);
                        wrapper.write(Type.FLAT_VAR_INT_ITEM, icon);
                        wrapper.passthrough((Type)Type.VAR_INT);
                        int flags = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                        if ((flags & 0x1) != 0)
                          wrapper.passthrough(Type.STRING); 
                        wrapper.passthrough((Type)Type.FLOAT);
                        wrapper.passthrough((Type)Type.FLOAT);
                      } 
                      wrapper.passthrough(Type.STRING_ARRAY);
                      int arrayLength = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      for (int array = 0; array < arrayLength; array++)
                        wrapper.passthrough(Type.STRING_ARRAY); 
                    } 
                  }
                });
          }
        });
  }
}
