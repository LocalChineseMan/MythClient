package com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class Protocol1_14_4To1_14_3 extends AbstractProtocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {
  public Protocol1_14_4To1_14_3() {
    super(ClientboundPackets1_14.class, ClientboundPackets1_14.class, null, null);
  }
  
  protected void registerPackets() {
    registerClientbound((ClientboundPacketType)ClientboundPackets1_14.TRADE_LIST, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.passthrough((Type)Type.VAR_INT);
                    int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                    for (int i = 0; i < size; i++) {
                      wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                      wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                      if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                        wrapper.passthrough(Type.FLAT_VAR_INT_ITEM); 
                      wrapper.passthrough((Type)Type.BOOLEAN);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.passthrough((Type)Type.INT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.write((Type)Type.INT, Integer.valueOf(0));
                    } 
                  }
                });
          }
        });
  }
}
