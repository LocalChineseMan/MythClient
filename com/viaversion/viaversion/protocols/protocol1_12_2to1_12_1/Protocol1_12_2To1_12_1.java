package com.viaversion.viaversion.protocols.protocol1_12_2to1_12_1;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;

public class Protocol1_12_2To1_12_1 extends AbstractProtocol<ClientboundPackets1_12_1, ClientboundPackets1_12_1, ServerboundPackets1_12_1, ServerboundPackets1_12_1> {
  public Protocol1_12_2To1_12_1() {
    super(ClientboundPackets1_12_1.class, ClientboundPackets1_12_1.class, ServerboundPackets1_12_1.class, ServerboundPackets1_12_1.class);
  }
  
  protected void registerPackets() {
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, (Type)Type.LONG);
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.LONG, (Type)Type.VAR_INT);
          }
        });
  }
}
