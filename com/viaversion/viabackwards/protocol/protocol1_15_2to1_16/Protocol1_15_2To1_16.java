package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.chat.TranslatableRewriter1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.BackwardsMappings;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.CommandRewriter1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.WorldNameTracker;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets.BlockItemPackets1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets.EntityPackets1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.storage.PlayerSneakStorage;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import com.viaversion.viaversion.util.GsonUtil;
import java.util.Objects;
import java.util.UUID;

public class Protocol1_15_2To1_16 extends BackwardsProtocol<ClientboundPackets1_16, ClientboundPackets1_15, ServerboundPackets1_16, ServerboundPackets1_14> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings();
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets1_16(this);
  
  private BlockItemPackets1_16 blockItemPackets;
  
  private TranslatableRewriter translatableRewriter;
  
  public Protocol1_15_2To1_16() {
    super(ClientboundPackets1_16.class, ClientboundPackets1_15.class, ServerboundPackets1_16.class, ServerboundPackets1_14.class);
  }
  
  protected void registerPackets() {
    Objects.requireNonNull(MAPPINGS);
    executeAsyncAfterLoaded(Protocol1_16To1_15_2.class, MAPPINGS::load);
    this.translatableRewriter = (TranslatableRewriter)new TranslatableRewriter1_16(this);
    this.translatableRewriter.registerBossBar((ClientboundPacketType)ClientboundPackets1_16.BOSSBAR);
    this.translatableRewriter.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_16.COMBAT_EVENT);
    this.translatableRewriter.registerDisconnect((ClientboundPacketType)ClientboundPackets1_16.DISCONNECT);
    this.translatableRewriter.registerTabList((ClientboundPacketType)ClientboundPackets1_16.TAB_LIST);
    this.translatableRewriter.registerTitle((ClientboundPacketType)ClientboundPackets1_16.TITLE);
    this.translatableRewriter.registerPing();
    (new CommandRewriter1_16((Protocol)this)).registerDeclareCommands((ClientboundPacketType)ClientboundPackets1_16.DECLARE_COMMANDS);
    (this.blockItemPackets = new BlockItemPackets1_16(this, this.translatableRewriter)).register();
    this.entityRewriter.register();
    registerClientbound(State.STATUS, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String original = (String)wrapper.passthrough(Type.STRING);
                  JsonObject object = (JsonObject)GsonUtil.getGson().fromJson(original, JsonObject.class);
                  JsonElement description = object.get("description");
                  if (description == null)
                    return; 
                  Protocol1_15_2To1_16.this.translatableRewriter.processText(description);
                  wrapper.set(Type.STRING, 0, object.toString());
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> Protocol1_15_2To1_16.this.translatableRewriter.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)));
            map((Type)Type.BYTE);
            map(Type.UUID, (Type)Type.NOTHING);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(wrapper -> Protocol1_15_2To1_16.this.translatableRewriter.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)));
            handler(wrapper -> {
                  int windowType = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                  if (windowType == 20) {
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(7));
                  } else if (windowType > 20) {
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(--windowType));
                  } 
                });
          }
        });
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.ENTITY_SOUND);
    soundRewriter.registerNamedSound((ClientboundPacketType)ClientboundPackets1_16.NAMED_SOUND);
    soundRewriter.registerStopSound((ClientboundPacketType)ClientboundPackets1_16.STOP_SOUND);
    registerClientbound(State.LOGIN, 2, 2, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  UUID uuid = (UUID)wrapper.read(Type.UUID_INT_ARRAY);
                  wrapper.write(Type.STRING, uuid.toString());
                });
          }
        });
    (new TagRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16.TAGS, RegistryType.ENTITY);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16.STATISTICS);
    registerServerbound((ServerboundPacketType)ServerboundPackets1_14.ENTITY_ACTION, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int action = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  if (action == 0) {
                    ((PlayerSneakStorage)wrapper.user().get(PlayerSneakStorage.class)).setSneaking(true);
                  } else if (action == 1) {
                    ((PlayerSneakStorage)wrapper.user().get(PlayerSneakStorage.class)).setSneaking(false);
                  } 
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_14.INTERACT_ENTITY, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int action = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  if (action == 0 || action == 2) {
                    if (action == 2) {
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                    } 
                    wrapper.passthrough((Type)Type.VAR_INT);
                  } 
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((PlayerSneakStorage)wrapper.user().get(PlayerSneakStorage.class)).isSneaking()));
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_14.PLAYER_ABILITIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  byte flags = ((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                  flags = (byte)(flags & 0x2);
                  wrapper.write((Type)Type.BYTE, Byte.valueOf(flags));
                  wrapper.read((Type)Type.FLOAT);
                  wrapper.read((Type)Type.FLOAT);
                });
          }
        });
    cancelServerbound((ServerboundPacketType)ServerboundPackets1_14.UPDATE_JIGSAW_BLOCK);
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StorableObject)new ClientWorld(user)); 
    user.put((StorableObject)new PlayerSneakStorage());
    user.put((StorableObject)new WorldNameTracker());
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_16Types.PLAYER));
  }
  
  public TranslatableRewriter getTranslatableRewriter() {
    return this.translatableRewriter;
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public BlockItemPackets1_16 getItemRewriter() {
    return this.blockItemPackets;
  }
}
