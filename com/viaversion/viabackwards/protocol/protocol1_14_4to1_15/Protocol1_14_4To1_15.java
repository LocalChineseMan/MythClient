package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.data.ImmediateRespawn;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets.BlockItemPackets1_15;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets.EntityPackets1_15;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.util.Objects;

public class Protocol1_14_4To1_15 extends BackwardsProtocol<ClientboundPackets1_15, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.15", "1.14", Protocol1_15To1_14_4.class, true);
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets1_15(this);
  
  private BlockItemPackets1_15 blockItemPackets;
  
  public Protocol1_14_4To1_15() {
    super(ClientboundPackets1_15.class, ClientboundPackets1_14.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
  }
  
  protected void registerPackets() {
    Objects.requireNonNull(MAPPINGS);
    executeAsyncAfterLoaded(Protocol1_15To1_14_4.class, MAPPINGS::load);
    TranslatableRewriter translatableRewriter = new TranslatableRewriter(this);
    translatableRewriter.registerBossBar((ClientboundPacketType)ClientboundPackets1_15.BOSSBAR);
    translatableRewriter.registerChatMessage((ClientboundPacketType)ClientboundPackets1_15.CHAT_MESSAGE);
    translatableRewriter.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_15.COMBAT_EVENT);
    translatableRewriter.registerDisconnect((ClientboundPacketType)ClientboundPackets1_15.DISCONNECT);
    translatableRewriter.registerOpenWindow((ClientboundPacketType)ClientboundPackets1_15.OPEN_WINDOW);
    translatableRewriter.registerTabList((ClientboundPacketType)ClientboundPackets1_15.TAB_LIST);
    translatableRewriter.registerTitle((ClientboundPacketType)ClientboundPackets1_15.TITLE);
    translatableRewriter.registerPing();
    (this.blockItemPackets = new BlockItemPackets1_15(this, translatableRewriter)).register();
    this.entityRewriter.register();
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_SOUND);
    soundRewriter.registerNamedSound((ClientboundPacketType)ClientboundPackets1_15.NAMED_SOUND);
    soundRewriter.registerStopSound((ClientboundPacketType)ClientboundPackets1_15.STOP_SOUND);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_15.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(wrapper -> {
                  PacketWrapper soundPacket = wrapper.create((PacketType)ClientboundPackets1_14.SOUND);
                  soundPacket.write((Type)Type.VAR_INT, Integer.valueOf(243));
                  soundPacket.write((Type)Type.VAR_INT, Integer.valueOf(4));
                  soundPacket.write((Type)Type.INT, Integer.valueOf(toEffectCoordinate(((Float)wrapper.get((Type)Type.FLOAT, 0)).floatValue())));
                  soundPacket.write((Type)Type.INT, Integer.valueOf(toEffectCoordinate(((Float)wrapper.get((Type)Type.FLOAT, 1)).floatValue())));
                  soundPacket.write((Type)Type.INT, Integer.valueOf(toEffectCoordinate(((Float)wrapper.get((Type)Type.FLOAT, 2)).floatValue())));
                  soundPacket.write((Type)Type.FLOAT, Float.valueOf(4.0F));
                  soundPacket.write((Type)Type.FLOAT, Float.valueOf(1.0F));
                  soundPacket.send(Protocol1_14_4To1_15.class);
                });
          }
          
          private int toEffectCoordinate(float coordinate) {
            return (int)(coordinate * 8.0F);
          }
        });
    (new TagRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_15.TAGS, RegistryType.ENTITY);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_15.STATISTICS);
  }
  
  public void init(UserConnection user) {
    user.put((StorableObject)new ImmediateRespawn());
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_15Types.PLAYER));
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public BlockItemPackets1_15 getItemRewriter() {
    return this.blockItemPackets;
  }
}
