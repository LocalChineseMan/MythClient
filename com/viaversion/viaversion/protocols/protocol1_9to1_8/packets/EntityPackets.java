package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.google.common.collect.ImmutableList;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.util.Pair;
import com.viaversion.viaversion.util.Triple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityPackets {
  public static final ValueTransformer<Byte, Short> toNewShort = new ValueTransformer<Byte, Short>((Type)Type.SHORT) {
      public Short transform(PacketWrapper wrapper, Byte inputValue) {
        return Short.valueOf((short)(inputValue.byteValue() * 128));
      }
    };
  
  public static void register(final Protocol1_9To1_8 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ATTACH_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN, new ValueTransformer<Boolean, Void>((Type)Type.NOTHING) {
                  public Void transform(PacketWrapper wrapper, Boolean inputValue) throws Exception {
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (!inputValue.booleanValue()) {
                      int passenger = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                      int vehicle = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                      wrapper.cancel();
                      PacketWrapper passengerPacket = wrapper.create((PacketType)ClientboundPackets1_9.SET_PASSENGERS);
                      if (vehicle == -1) {
                        if (!tracker.getVehicleMap().containsKey(Integer.valueOf(passenger)))
                          return null; 
                        passengerPacket.write((Type)Type.VAR_INT, tracker.getVehicleMap().remove(Integer.valueOf(passenger)));
                        passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[0]);
                      } else {
                        passengerPacket.write((Type)Type.VAR_INT, Integer.valueOf(vehicle));
                        passengerPacket.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { passenger });
                        tracker.getVehicleMap().put(Integer.valueOf(passenger), Integer.valueOf(vehicle));
                      } 
                      passengerPacket.send(Protocol1_9To1_8.class);
                    } 
                    return null;
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_TELEPORT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (Via.getConfig().isHologramPatch()) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      if (tracker.getKnownHolograms().contains(Integer.valueOf(entityID))) {
                        Double newValue = (Double)wrapper.get((Type)Type.DOUBLE, 1);
                        newValue = Double.valueOf(newValue.doubleValue() + Via.getConfig().getHologramYOffset());
                        wrapper.set((Type)Type.DOUBLE, 1, newValue);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_POSITION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BYTE, EntityPackets.toNewShort);
            map((Type)Type.BOOLEAN);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.SHORT, new ValueTransformer<Short, Integer>((Type)Type.VAR_INT) {
                  public Integer transform(PacketWrapper wrapper, Short slot) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int receiverId = wrapper.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId();
                    if (entityId == receiverId)
                      return Integer.valueOf(slot.intValue() + 2); 
                    return Integer.valueOf((slot.shortValue() > 0) ? (slot.intValue() + 1) : slot.intValue());
                  }
                });
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    ItemRewriter.toClient(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    if (stack != null && 
                      Protocol1_9To1_8.isSword(stack.identifier())) {
                      entityTracker.getValidBlocking().add(Integer.valueOf(entityID));
                      return;
                    } 
                    entityTracker.getValidBlocking().remove(Integer.valueOf(entityID));
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_METADATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (tracker.hasEntity(entityId)) {
                      ((MetadataRewriter1_9To1_8)protocol.get(MetadataRewriter1_9To1_8.class)).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                      tracker.addMetadataToBuffer(entityId, metadataList);
                      wrapper.cancel();
                    } 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.handleMetadata(entityID, metadataList);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    if (metadataList.isEmpty())
                      wrapper.cancel(); 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    boolean showParticles = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                    boolean newEffect = Via.getConfig().isNewEffectIndicator();
                    wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)(showParticles ? (newEffect ? 2 : 1) : 0)));
                  }
                });
          }
        });
    protocol.cancelClientbound((ClientboundPacketType)ClientboundPackets1_8.UPDATE_ENTITY_NBT);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.COMBAT_EVENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue() == 2) {
                      wrapper.passthrough((Type)Type.VAR_INT);
                      wrapper.passthrough((Type)Type.INT);
                      Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (!Via.getConfig().isMinimizeCooldown())
                      return; 
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue() != tracker.getProvidedEntityId())
                      return; 
                    int propertiesToRead = ((Integer)wrapper.read((Type)Type.INT)).intValue();
                    Map<String, Pair<Double, List<Triple<UUID, Double, Byte>>>> properties = new HashMap<>(propertiesToRead);
                    for (int i = 0; i < propertiesToRead; i++) {
                      String key = (String)wrapper.read(Type.STRING);
                      Double value = (Double)wrapper.read((Type)Type.DOUBLE);
                      int modifiersToRead = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      List<Triple<UUID, Double, Byte>> modifiers = new ArrayList<>(modifiersToRead);
                      for (int j = 0; j < modifiersToRead; j++)
                        modifiers.add(new Triple(wrapper
                              
                              .read(Type.UUID), wrapper
                              .read((Type)Type.DOUBLE), wrapper
                              .read((Type)Type.BYTE))); 
                      properties.put(key, new Pair(value, modifiers));
                    } 
                    properties.put("generic.attackSpeed", new Pair(Double.valueOf(15.9D), ImmutableList.of(new Triple(
                              UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), Double.valueOf(0.0D), Byte.valueOf((byte)0)), new Triple(
                              UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3"), Double.valueOf(0.0D), Byte.valueOf((byte)2)), new Triple(
                              UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386"), Double.valueOf(0.0D), Byte.valueOf((byte)2)))));
                    wrapper.write((Type)Type.INT, Integer.valueOf(properties.size()));
                    for (Map.Entry<String, Pair<Double, List<Triple<UUID, Double, Byte>>>> entry : properties.entrySet()) {
                      wrapper.write(Type.STRING, entry.getKey());
                      wrapper.write((Type)Type.DOUBLE, ((Pair)entry.getValue()).getKey());
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(((List)((Pair)entry.getValue()).getValue()).size()));
                      for (Triple<UUID, Double, Byte> modifier : (Iterable<Triple<UUID, Double, Byte>>)((Pair)entry.getValue()).getValue()) {
                        wrapper.write(Type.UUID, modifier.getFirst());
                        wrapper.write((Type)Type.DOUBLE, modifier.getSecond());
                        wrapper.write((Type)Type.BYTE, modifier.getThird());
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.ENTITY_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 3)
                      wrapper.cancel(); 
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.ENTITY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    if (action == 6 || action == 8)
                      wrapper.cancel(); 
                    if (action == 7)
                      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(6)); 
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.INTERACT_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    if (type == 2) {
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                    } 
                    if (type == 0 || type == 2) {
                      int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      if (hand == 1)
                        wrapper.cancel(); 
                    } 
                  }
                });
          }
        });
  }
}
