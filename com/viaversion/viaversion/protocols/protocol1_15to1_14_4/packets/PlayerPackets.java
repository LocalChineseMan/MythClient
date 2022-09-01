package com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;

public class PlayerPackets {
  public static void register(Protocol protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(wrapper -> wrapper.write((Type)Type.LONG, Long.valueOf(0L)));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_14.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(wrapper -> {
                  EntityTracker tracker = wrapper.user().getEntityTracker(Protocol1_15To1_14_4.class);
                  int entityId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  tracker.addEntity(entityId, (EntityType)Entity1_15Types.PLAYER);
                });
            handler(wrapper -> wrapper.write((Type)Type.LONG, Long.valueOf(0L)));
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(!Via.getConfig().is1_15InstantRespawn())));
          }
        });
  }
}
