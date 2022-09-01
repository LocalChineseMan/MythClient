package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets.BlockItemPackets1_17;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets.EntityPackets1_17;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.storage.PingRequests;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_17Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.rewriter.IdRewriteFunction;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Protocol1_16_4To1_17 extends BackwardsProtocol<ClientboundPackets1_17, ClientboundPackets1_16_2, ServerboundPackets1_17, ServerboundPackets1_16_2> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.17", "1.16.2", Protocol1_17To1_16_4.class, true);
  
  private static final int[] EMPTY_ARRAY = new int[0];
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets1_17(this);
  
  private BlockItemPackets1_17 blockItemPackets;
  
  private TranslatableRewriter translatableRewriter;
  
  public Protocol1_16_4To1_17() {
    super(ClientboundPackets1_17.class, ClientboundPackets1_16_2.class, ServerboundPackets1_17.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    Objects.requireNonNull(MAPPINGS);
    executeAsyncAfterLoaded(Protocol1_17To1_16_4.class, MAPPINGS::load);
    this.translatableRewriter = new TranslatableRewriter(this);
    this.translatableRewriter.registerChatMessage((ClientboundPacketType)ClientboundPackets1_17.CHAT_MESSAGE);
    this.translatableRewriter.registerBossBar((ClientboundPacketType)ClientboundPackets1_17.BOSSBAR);
    this.translatableRewriter.registerDisconnect((ClientboundPacketType)ClientboundPackets1_17.DISCONNECT);
    this.translatableRewriter.registerTabList((ClientboundPacketType)ClientboundPackets1_17.TAB_LIST);
    this.translatableRewriter.registerOpenWindow((ClientboundPacketType)ClientboundPackets1_17.OPEN_WINDOW);
    this.translatableRewriter.registerPing();
    this.blockItemPackets = new BlockItemPackets1_17(this, this.translatableRewriter);
    this.blockItemPackets.register();
    this.entityRewriter.register();
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_17.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_17.ENTITY_SOUND);
    soundRewriter.registerNamedSound((ClientboundPacketType)ClientboundPackets1_17.NAMED_SOUND);
    soundRewriter.registerStopSound((ClientboundPacketType)ClientboundPackets1_17.STOP_SOUND);
    final TagRewriter tagRewriter = new TagRewriter((Protocol)this);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.TAGS, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Map<String, List<TagData>> tags = new HashMap<>();
                  int length = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < length; i++) {
                    String resourceKey = (String)wrapper.read(Type.STRING);
                    if (resourceKey.startsWith("minecraft:"))
                      resourceKey = resourceKey.substring(10); 
                    List<TagData> tagList = new ArrayList<>();
                    tags.put(resourceKey, tagList);
                    int tagLength = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    for (int j = 0; j < tagLength; j++) {
                      String identifier = (String)wrapper.read(Type.STRING);
                      int[] entries = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
                      tagList.add(new TagData(identifier, entries));
                    } 
                  } 
                  for (RegistryType type : RegistryType.getValues()) {
                    List<TagData> tagList = tags.get(type.getResourceLocation());
                    IdRewriteFunction rewriter = tagRewriter.getRewriter(type);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(tagList.size()));
                    for (TagData tagData : tagList) {
                      int[] entries = tagData.entries();
                      if (rewriter != null) {
                        IntArrayList intArrayList = new IntArrayList(entries.length);
                        for (int id : entries) {
                          int mappedId = rewriter.rewrite(id);
                          if (mappedId != -1)
                            intArrayList.add(mappedId); 
                        } 
                        entries = intArrayList.toArray(Protocol1_16_4To1_17.EMPTY_ARRAY);
                      } 
                      wrapper.write(Type.STRING, tagData.identifier());
                      wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, entries);
                    } 
                    if (type == RegistryType.ENTITY)
                      break; 
                  } 
                });
          }
        });
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_17.STATISTICS);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.RESOURCE_PACK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.read((Type)Type.BOOLEAN);
                  wrapper.read(Type.OPTIONAL_COMPONENT);
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(wrapper -> wrapper.write((Type)Type.INT, wrapper.read((Type)Type.VAR_INT)));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.SPAWN_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14);
            handler(wrapper -> wrapper.read((Type)Type.FLOAT));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.PING, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.cancel();
                  int id = ((Integer)wrapper.read((Type)Type.INT)).intValue();
                  short shortId = (short)id;
                  if (id == shortId && ViaBackwards.getConfig().handlePingsAsInvAcknowledgements()) {
                    ((PingRequests)wrapper.user().get(PingRequests.class)).addId(shortId);
                    PacketWrapper acknowledgementPacket = wrapper.create((PacketType)ClientboundPackets1_16_2.WINDOW_CONFIRMATION);
                    acknowledgementPacket.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    acknowledgementPacket.write((Type)Type.SHORT, Short.valueOf(shortId));
                    acknowledgementPacket.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                    acknowledgementPacket.send(Protocol1_16_4To1_17.class);
                    return;
                  } 
                  PacketWrapper pongPacket = wrapper.create((PacketType)ServerboundPackets1_17.PONG);
                  pongPacket.write((Type)Type.INT, Integer.valueOf(id));
                  pongPacket.sendToServer(Protocol1_16_4To1_17.class);
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.CLIENT_SETTINGS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false)));
          }
        });
    mergePacket(ClientboundPackets1_17.TITLE_TEXT, ClientboundPackets1_16_2.TITLE, 0);
    mergePacket(ClientboundPackets1_17.TITLE_SUBTITLE, ClientboundPackets1_16_2.TITLE, 1);
    mergePacket(ClientboundPackets1_17.ACTIONBAR, ClientboundPackets1_16_2.TITLE, 2);
    mergePacket(ClientboundPackets1_17.TITLE_TIMES, ClientboundPackets1_16_2.TITLE, 3);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_17.CLEAR_TITLES, (ClientboundPacketType)ClientboundPackets1_16_2.TITLE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  if (((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue()) {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(5));
                  } else {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(4));
                  } 
                });
          }
        });
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_17.ADD_VIBRATION_SIGNAL);
  }
  
  public void init(UserConnection user) {
    addEntityTracker(user, (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_17Types.PLAYER));
    user.put((StorableObject)new PingRequests());
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public TranslatableRewriter getTranslatableRewriter() {
    return this.translatableRewriter;
  }
  
  public void mergePacket(ClientboundPackets1_17 newPacketType, ClientboundPackets1_16_2 oldPacketType, final int type) {
    registerClientbound((ClientboundPacketType)newPacketType, (ClientboundPacketType)oldPacketType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> wrapper.write((Type)Type.VAR_INT, Integer.valueOf(type)));
          }
        });
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return (ItemRewriter)this.blockItemPackets;
  }
}
