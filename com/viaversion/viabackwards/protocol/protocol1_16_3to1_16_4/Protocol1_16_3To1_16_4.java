package com.viaversion.viabackwards.protocol.protocol1_16_3to1_16_4;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.protocol.protocol1_16_3to1_16_4.storage.PlayerHandStorage;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;

public class Protocol1_16_3To1_16_4 extends BackwardsProtocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> {
  public Protocol1_16_3To1_16_4() {
    super(ClientboundPackets1_16_2.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            map(Type.FLAT_VAR_INT_ITEM);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  int slot = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  if (slot == 1) {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(40));
                  } else {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(((PlayerHandStorage)wrapper.user().get(PlayerHandStorage.class)).getCurrentHand()));
                  } 
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.HELD_ITEM_CHANGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  short slot = ((Short)wrapper.passthrough((Type)Type.SHORT)).shortValue();
                  ((PlayerHandStorage)wrapper.user().get(PlayerHandStorage.class)).setCurrentHand(slot);
                });
          }
        });
  }
  
  public void init(UserConnection user) {
    user.put((StorableObject)new PlayerHandStorage());
  }
}
