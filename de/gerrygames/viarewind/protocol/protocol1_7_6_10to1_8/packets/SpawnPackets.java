package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.ArmorStandReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.EndermiteReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.GuardianReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items.ReplacementRegistry1_7_6_10to1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.metadata.MetadataRewriter;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import de.gerrygames.viarewind.replacement.Replacement;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.List;
import java.util.UUID;

public class SpawnPackets {
  public static void register(Protocol1_7_6_10TO1_8 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(packetWrapper -> {
                  UUID uuid = (UUID)packetWrapper.read(Type.UUID);
                  packetWrapper.write(Type.STRING, uuid.toString());
                  GameProfileStorage gameProfileStorage = (GameProfileStorage)packetWrapper.user().get(GameProfileStorage.class);
                  GameProfileStorage.GameProfile gameProfile = gameProfileStorage.get(uuid);
                  if (gameProfile == null) {
                    packetWrapper.write(Type.STRING, "");
                    packetWrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  } else {
                    packetWrapper.write(Type.STRING, (gameProfile.name.length() > 16) ? gameProfile.name.substring(0, 16) : gameProfile.name);
                    packetWrapper.write((Type)Type.VAR_INT, Integer.valueOf(gameProfile.properties.size()));
                    for (GameProfileStorage.Property property : gameProfile.properties) {
                      packetWrapper.write(Type.STRING, property.name);
                      packetWrapper.write(Type.STRING, property.value);
                      packetWrapper.write(Type.STRING, (property.signature == null) ? "" : property.signature);
                    } 
                  } 
                  if (gameProfile != null && gameProfile.gamemode == 3) {
                    int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    PacketWrapper equipmentPacket = PacketWrapper.create(4, null, packetWrapper.user());
                    equipmentPacket.write((Type)Type.INT, Integer.valueOf(entityId));
                    equipmentPacket.write((Type)Type.SHORT, Short.valueOf((short)4));
                    equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, gameProfile.getSkull());
                    PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10TO1_8.class);
                    short i;
                    for (i = 0; i < 4; i = (short)(i + 1)) {
                      equipmentPacket = PacketWrapper.create(4, null, packetWrapper.user());
                      equipmentPacket.write((Type)Type.INT, Integer.valueOf(entityId));
                      equipmentPacket.write((Type)Type.SHORT, Short.valueOf(i));
                      equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, null);
                      PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10TO1_8.class);
                    } 
                  } 
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.addPlayer((Integer)packetWrapper.get((Type)Type.VAR_INT, 0), uuid);
                });
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map(Types1_8.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            handler(packetWrapper -> {
                  List<Metadata> metadata = (List<Metadata>)packetWrapper.get(Types1_7_6_10.METADATA_LIST, 0);
                  MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadata);
                });
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), Entity1_10Types.EntityType.PLAYER);
                  tracker.sendMetadataBuffer(entityId);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  byte typeId = ((Byte)packetWrapper.get((Type)Type.BYTE, 0)).byteValue();
                  int x = ((Integer)packetWrapper.get((Type)Type.INT, 0)).intValue();
                  int y = ((Integer)packetWrapper.get((Type)Type.INT, 1)).intValue();
                  int z = ((Integer)packetWrapper.get((Type)Type.INT, 2)).intValue();
                  byte pitch = ((Byte)packetWrapper.get((Type)Type.BYTE, 1)).byteValue();
                  byte yaw = ((Byte)packetWrapper.get((Type)Type.BYTE, 2)).byteValue();
                  if (typeId == 71) {
                    switch (yaw) {
                      case -128:
                        z += 32;
                        yaw = 0;
                        break;
                      case -64:
                        x -= 32;
                        yaw = -64;
                        break;
                      case 0:
                        z -= 32;
                        yaw = Byte.MIN_VALUE;
                        break;
                      case 64:
                        x += 32;
                        yaw = 64;
                        break;
                    } 
                  } else if (typeId == 78) {
                    packetWrapper.cancel();
                    EntityTracker entityTracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                    ArmorStandReplacement armorStand = new ArmorStandReplacement(entityId, packetWrapper.user());
                    armorStand.setLocation(x / 32.0D, y / 32.0D, z / 32.0D);
                    armorStand.setYawPitch(yaw * 360.0F / 256.0F, pitch * 360.0F / 256.0F);
                    armorStand.setHeadYaw(yaw * 360.0F / 256.0F);
                    entityTracker.addEntityReplacement((EntityReplacement)armorStand);
                  } else if (typeId == 10) {
                    y += 12;
                  } 
                  packetWrapper.set((Type)Type.BYTE, 0, Byte.valueOf(typeId));
                  packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(x));
                  packetWrapper.set((Type)Type.INT, 1, Integer.valueOf(y));
                  packetWrapper.set((Type)Type.INT, 2, Integer.valueOf(z));
                  packetWrapper.set((Type)Type.BYTE, 1, Byte.valueOf(pitch));
                  packetWrapper.set((Type)Type.BYTE, 2, Byte.valueOf(yaw));
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeId, true);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), type);
                  tracker.sendMetadataBuffer(entityId);
                  int data = ((Integer)packetWrapper.get((Type)Type.INT, 3)).intValue();
                  if (type != null && type.isOrHasParent((EntityType)Entity1_10Types.EntityType.FALLING_BLOCK)) {
                    int blockId = data & 0xFFF;
                    int blockData = data >> 12 & 0xF;
                    Replacement replace = ReplacementRegistry1_7_6_10to1_8.getReplacement(blockId, blockData);
                    if (replace != null) {
                      blockId = replace.getId();
                      blockData = replace.replaceData(blockData);
                    } 
                    packetWrapper.set((Type)Type.INT, 3, Integer.valueOf(data = blockId | blockData << 16));
                  } 
                  if (data > 0) {
                    packetWrapper.passthrough((Type)Type.SHORT);
                    packetWrapper.passthrough((Type)Type.SHORT);
                    packetWrapper.passthrough((Type)Type.SHORT);
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_8.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int typeId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  int x = ((Integer)packetWrapper.get((Type)Type.INT, 0)).intValue();
                  int y = ((Integer)packetWrapper.get((Type)Type.INT, 1)).intValue();
                  int z = ((Integer)packetWrapper.get((Type)Type.INT, 2)).intValue();
                  byte pitch = ((Byte)packetWrapper.get((Type)Type.BYTE, 1)).byteValue();
                  byte yaw = ((Byte)packetWrapper.get((Type)Type.BYTE, 0)).byteValue();
                  byte headYaw = ((Byte)packetWrapper.get((Type)Type.BYTE, 2)).byteValue();
                  if (typeId == 30) {
                    packetWrapper.cancel();
                    EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                    ArmorStandReplacement armorStand = new ArmorStandReplacement(entityId, packetWrapper.user());
                    armorStand.setLocation(x / 32.0D, y / 32.0D, z / 32.0D);
                    armorStand.setYawPitch(yaw * 360.0F / 256.0F, pitch * 360.0F / 256.0F);
                    armorStand.setHeadYaw(headYaw * 360.0F / 256.0F);
                    tracker.addEntityReplacement((EntityReplacement)armorStand);
                  } else if (typeId == 68) {
                    packetWrapper.cancel();
                    EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                    GuardianReplacement guardian = new GuardianReplacement(entityId, packetWrapper.user());
                    guardian.setLocation(x / 32.0D, y / 32.0D, z / 32.0D);
                    guardian.setYawPitch(yaw * 360.0F / 256.0F, pitch * 360.0F / 256.0F);
                    guardian.setHeadYaw(headYaw * 360.0F / 256.0F);
                    tracker.addEntityReplacement((EntityReplacement)guardian);
                  } else if (typeId == 67) {
                    packetWrapper.cancel();
                    EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                    EndermiteReplacement endermite = new EndermiteReplacement(entityId, packetWrapper.user());
                    endermite.setLocation(x / 32.0D, y / 32.0D, z / 32.0D);
                    endermite.setYawPitch(yaw * 360.0F / 256.0F, pitch * 360.0F / 256.0F);
                    endermite.setHeadYaw(headYaw * 360.0F / 256.0F);
                    tracker.addEntityReplacement((EntityReplacement)endermite);
                  } else if (typeId == 101 || typeId == 255 || typeId == -1) {
                    packetWrapper.cancel();
                  } 
                });
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int typeId = ((Short)packetWrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), Entity1_10Types.getTypeFromId(typeId, false));
                  tracker.sendMetadataBuffer(entityId);
                });
            handler(wrapper -> {
                  List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_7_6_10.METADATA_LIST, 0);
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityTracker tracker = (EntityTracker)wrapper.user().get(EntityTracker.class);
                  if (tracker.getEntityReplacement(entityId) != null) {
                    tracker.getEntityReplacement(entityId).updateMetadata(metadataList);
                  } else if (tracker.getClientEntityTypes().containsKey(Integer.valueOf(entityId))) {
                    MetadataRewriter.transform((Entity1_10Types.EntityType)tracker.getClientEntityTypes().get(Integer.valueOf(entityId)), metadataList);
                  } else {
                    wrapper.cancel();
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.STRING);
            handler(packetWrapper -> {
                  Position position = (Position)packetWrapper.read(Type.POSITION);
                  packetWrapper.write((Type)Type.INT, Integer.valueOf(position.getX()));
                  packetWrapper.write((Type)Type.INT, Integer.valueOf(position.getY()));
                  packetWrapper.write((Type)Type.INT, Integer.valueOf(position.getZ()));
                });
            map((Type)Type.UNSIGNED_BYTE, (Type)Type.INT);
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), Entity1_10Types.EntityType.PAINTING);
                  tracker.sendMetadataBuffer(entityId);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.SHORT);
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), Entity1_10Types.EntityType.EXPERIENCE_ORB);
                  tracker.sendMetadataBuffer(entityId);
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            handler(packetWrapper -> {
                  int entityId = ((Integer)packetWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
                  tracker.getClientEntityTypes().put(Integer.valueOf(entityId), Entity1_10Types.EntityType.LIGHTNING);
                  tracker.sendMetadataBuffer(entityId);
                });
          }
        });
  }
}
