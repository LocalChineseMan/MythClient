package com.viaversion.viabackwards.protocol.protocol1_12_1to1_12_2;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;

public class Protocol1_12_1To1_12_2 extends BackwardsProtocol<ClientboundPackets1_12_1, ClientboundPackets1_12_1, ServerboundPackets1_12_1, ServerboundPackets1_12_1> {
  public Protocol1_12_1To1_12_2() {
    super(ClientboundPackets1_12_1.class, ClientboundPackets1_12_1.class, ServerboundPackets1_12_1.class, ServerboundPackets1_12_1.class);
  }
  
  protected void registerPackets() {
    registerClientbound((ClientboundPacketType)ClientboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper packetWrapper) throws Exception {
                    Long keepAlive = (Long)packetWrapper.read((Type)Type.LONG);
                    ((KeepAliveTracker)packetWrapper.user().get(KeepAliveTracker.class)).setKeepAlive(keepAlive.longValue());
                    packetWrapper.write((Type)Type.VAR_INT, Integer.valueOf(keepAlive.hashCode()));
                  }
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper packetWrapper) throws Exception {
                    int keepAlive = ((Integer)packetWrapper.read((Type)Type.VAR_INT)).intValue();
                    long realKeepAlive = ((KeepAliveTracker)packetWrapper.user().get(KeepAliveTracker.class)).getKeepAlive();
                    if (keepAlive != Long.hashCode(realKeepAlive)) {
                      packetWrapper.cancel();
                      return;
                    } 
                    packetWrapper.write((Type)Type.LONG, Long.valueOf(realKeepAlive));
                    ((KeepAliveTracker)packetWrapper.user().get(KeepAliveTracker.class)).setKeepAlive(2147483647L);
                  }
                });
          }
        });
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StorableObject)new KeepAliveTracker());
  }
}
