package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.BackwardsMappings;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.PaintingMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets.BlockItemPackets1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets.EntityPackets1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets.PlayerPacket1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets.SoundPackets1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.PlayerPositionStorage1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.TabCompleteStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Protocol1_12_2To1_13 extends BackwardsProtocol<ClientboundPackets1_13, ClientboundPackets1_12_1, ServerboundPackets1_13, ServerboundPackets1_12_1> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings();
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new EntityPackets1_13(this);
  
  private final BlockItemPackets1_13 blockItemPackets = new BlockItemPackets1_13(this);
  
  public Protocol1_12_2To1_13() {
    super(ClientboundPackets1_13.class, ClientboundPackets1_12_1.class, ServerboundPackets1_13.class, ServerboundPackets1_12_1.class);
  }
  
  protected void registerPackets() {
    executeAsyncAfterLoaded(Protocol1_13To1_12_2.class, () -> {
          MAPPINGS.load();
          PaintingMapping.init();
          Via.getManager().getProviders().register(BackwardsBlockEntityProvider.class, (Provider)new BackwardsBlockEntityProvider());
        });
    TranslatableRewriter translatableRewriter = new TranslatableRewriter(this) {
        protected void handleTranslate(JsonObject root, String translate) {
          String newTranslate = (String)this.newTranslatables.get(translate);
          if (newTranslate != null || (newTranslate = (String)Protocol1_12_2To1_13.this.getMappingData().getTranslateMappings().get(translate)) != null)
            root.addProperty("translate", newTranslate); 
        }
      };
    translatableRewriter.registerPing();
    translatableRewriter.registerBossBar((ClientboundPacketType)ClientboundPackets1_13.BOSSBAR);
    translatableRewriter.registerChatMessage((ClientboundPacketType)ClientboundPackets1_13.CHAT_MESSAGE);
    translatableRewriter.registerLegacyOpenWindow((ClientboundPacketType)ClientboundPackets1_13.OPEN_WINDOW);
    translatableRewriter.registerDisconnect((ClientboundPacketType)ClientboundPackets1_13.DISCONNECT);
    translatableRewriter.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_13.COMBAT_EVENT);
    translatableRewriter.registerTitle((ClientboundPacketType)ClientboundPackets1_13.TITLE);
    translatableRewriter.registerTabList((ClientboundPacketType)ClientboundPackets1_13.TAB_LIST);
    this.blockItemPackets.register();
    this.entityRewriter.register();
    (new PlayerPacket1_13(this)).register();
    (new SoundPackets1_13(this)).register();
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.NBT_QUERY);
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.CRAFT_RECIPE_RESPONSE);
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.UNLOCK_RECIPES);
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.ADVANCEMENTS);
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.DECLARE_RECIPES);
    cancelClientbound((ClientboundPacketType)ClientboundPackets1_13.TAGS);
    cancelServerbound((ServerboundPacketType)ServerboundPackets1_12_1.CRAFT_RECIPE_REQUEST);
    cancelServerbound((ServerboundPacketType)ServerboundPackets1_12_1.RECIPE_BOOK_DATA);
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StorableObject)new ClientWorld(user)); 
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_13Types.EntityType.PLAYER));
    user.put((StorableObject)new BackwardsBlockStorage());
    user.put((StorableObject)new TabCompleteStorage());
    if (ViaBackwards.getConfig().isFix1_13FacePlayer() && !user.has(PlayerPositionStorage1_13.class))
      user.put((StorableObject)new PlayerPositionStorage1_13()); 
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public BlockItemPackets1_13 getItemRewriter() {
    return this.blockItemPackets;
  }
}
