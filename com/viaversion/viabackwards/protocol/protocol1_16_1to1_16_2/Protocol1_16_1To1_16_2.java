package com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.data.CommandRewriter1_16_2;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets.BlockItemPackets1_16_2;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets.EntityPackets1_16_2;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.util.Objects;

public class Protocol1_16_1To1_16_2 extends BackwardsProtocol<ClientboundPackets1_16_2, ClientboundPackets1_16, ServerboundPackets1_16_2, ServerboundPackets1_16> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.16.2", "1.16", Protocol1_16_2To1_16_1.class, true);
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets1_16_2(this);
  
  private BlockItemPackets1_16_2 blockItemPackets;
  
  private TranslatableRewriter translatableRewriter;
  
  public Protocol1_16_1To1_16_2() {
    super(ClientboundPackets1_16_2.class, ClientboundPackets1_16.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16.class);
  }
  
  protected void registerPackets() {
    Objects.requireNonNull(MAPPINGS);
    executeAsyncAfterLoaded(Protocol1_16_2To1_16_1.class, MAPPINGS::load);
    this.translatableRewriter = new TranslatableRewriter(this);
    this.translatableRewriter.registerBossBar((ClientboundPacketType)ClientboundPackets1_16_2.BOSSBAR);
    this.translatableRewriter.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_16_2.COMBAT_EVENT);
    this.translatableRewriter.registerDisconnect((ClientboundPacketType)ClientboundPackets1_16_2.DISCONNECT);
    this.translatableRewriter.registerTabList((ClientboundPacketType)ClientboundPackets1_16_2.TAB_LIST);
    this.translatableRewriter.registerTitle((ClientboundPacketType)ClientboundPackets1_16_2.TITLE);
    this.translatableRewriter.registerOpenWindow((ClientboundPacketType)ClientboundPackets1_16_2.OPEN_WINDOW);
    this.translatableRewriter.registerPing();
    (new CommandRewriter1_16_2((Protocol)this)).registerDeclareCommands((ClientboundPacketType)ClientboundPackets1_16_2.DECLARE_COMMANDS);
    (this.blockItemPackets = new BlockItemPackets1_16_2(this, this.translatableRewriter)).register();
    this.entityRewriter.register();
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_SOUND);
    soundRewriter.registerNamedSound((ClientboundPacketType)ClientboundPackets1_16_2.NAMED_SOUND);
    soundRewriter.registerStopSound((ClientboundPacketType)ClientboundPackets1_16_2.STOP_SOUND);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  JsonElement message = (JsonElement)wrapper.passthrough(Type.COMPONENT);
                  Protocol1_16_1To1_16_2.this.translatableRewriter.processText(message);
                  byte position = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
                  if (position == 2) {
                    wrapper.clearPacket();
                    wrapper.setId(ClientboundPackets1_16.TITLE.ordinal());
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(2));
                    wrapper.write(Type.COMPONENT, message);
                  } 
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_16.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (type == 0) {
                      wrapper.passthrough(Type.STRING);
                      wrapper.setId(ServerboundPackets1_16_2.SEEN_RECIPE.ordinal());
                    } else {
                      wrapper.cancel();
                      for (int i = 0; i < 3; i++)
                        sendSeenRecipePacket(i, wrapper); 
                    } 
                  }
                  
                  private void sendSeenRecipePacket(int recipeType, PacketWrapper wrapper) throws Exception {
                    boolean open = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                    boolean filter = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                    PacketWrapper newPacket = wrapper.create((PacketType)ServerboundPackets1_16_2.RECIPE_BOOK_DATA);
                    newPacket.write((Type)Type.VAR_INT, Integer.valueOf(recipeType));
                    newPacket.write((Type)Type.BOOLEAN, Boolean.valueOf(open));
                    newPacket.write((Type)Type.BOOLEAN, Boolean.valueOf(filter));
                    newPacket.sendToServer(Protocol1_16_1To1_16_2.class);
                  }
                });
          }
        });
    (new TagRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16_2.TAGS, RegistryType.ENTITY);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_16_2.STATISTICS);
  }
  
  public void init(UserConnection user) {
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_16_2Types.PLAYER));
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
  
  public BlockItemPackets1_16_2 getItemRewriter() {
    return this.blockItemPackets;
  }
}
