package com.viaversion.viaversion.protocols.protocol1_16to1_15_2;

import com.google.common.base.Joiner;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.TranslationMappings;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.metadata.MetadataRewriter1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.WorldPackets;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import com.viaversion.viaversion.util.GsonUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protocol1_16To1_15_2 extends AbstractProtocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16> {
  private static final UUID ZERO_UUID = new UUID(0L, 0L);
  
  public static final MappingData MAPPINGS = new MappingData();
  
  private final EntityRewriter metadataRewriter = (EntityRewriter)new MetadataRewriter1_16To1_15_2(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  private TagRewriter tagRewriter;
  
  public Protocol1_16To1_15_2() {
    super(ClientboundPackets1_15.class, ClientboundPackets1_16.class, ServerboundPackets1_14.class, ServerboundPackets1_16.class);
  }
  
  protected void registerPackets() {
    this.metadataRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    WorldPackets.register(this);
    this.tagRewriter = new TagRewriter((Protocol)this);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_15.TAGS, RegistryType.ENTITY);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_15.STATISTICS);
    registerClientbound(State.LOGIN, 2, 2, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  UUID uuid = UUID.fromString((String)wrapper.read(Type.STRING));
                  wrapper.write(Type.UUID_INT_ARRAY, uuid);
                });
          }
        });
    registerClientbound(State.STATUS, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String original = (String)wrapper.passthrough(Type.STRING);
                  JsonObject object = (JsonObject)GsonUtil.getGson().fromJson(original, JsonObject.class);
                  JsonObject players = object.getAsJsonObject("players");
                  if (players == null)
                    return; 
                  JsonArray sample = players.getAsJsonArray("sample");
                  if (sample == null)
                    return; 
                  JsonArray splitSamples = new JsonArray();
                  for (JsonElement element : sample) {
                    JsonObject playerInfo = element.getAsJsonObject();
                    String name = playerInfo.getAsJsonPrimitive("name").getAsString();
                    if (name.indexOf('\n') == -1) {
                      splitSamples.add((JsonElement)playerInfo);
                      continue;
                    } 
                    String id = playerInfo.getAsJsonPrimitive("id").getAsString();
                    for (String s : name.split("\n")) {
                      JsonObject newSample = new JsonObject();
                      newSample.addProperty("name", s);
                      newSample.addProperty("id", id);
                      splitSamples.add((JsonElement)newSample);
                    } 
                  } 
                  if (splitSamples.size() != sample.size()) {
                    players.add("sample", (JsonElement)splitSamples);
                    wrapper.set(Type.STRING, 0, object.toString());
                  } 
                });
          }
        });
    final TranslationMappings componentRewriter = new TranslationMappings((Protocol)this);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_15.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.COMPONENT);
            map((Type)Type.BYTE);
            handler(wrapper -> {
                  componentRewriter.processText((JsonElement)wrapper.get(Type.COMPONENT, 0));
                  wrapper.write(Type.UUID, Protocol1_16To1_15_2.ZERO_UUID);
                });
          }
        });
    translationMappings.registerBossBar((ClientboundPacketType)ClientboundPackets1_15.BOSSBAR);
    translationMappings.registerTitle((ClientboundPacketType)ClientboundPackets1_15.TITLE);
    translationMappings.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_15.COMBAT_EVENT);
    SoundRewriter soundRewriter = new SoundRewriter((Protocol)this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_SOUND);
    registerServerbound(ServerboundPackets1_16.INTERACT_ENTITY, new PacketRemapper() {
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
                  wrapper.read((Type)Type.BOOLEAN);
                });
          }
        });
    if (Via.getConfig().isIgnoreLong1_16ChannelNames())
      registerServerbound(ServerboundPackets1_16.PLUGIN_MESSAGE, new PacketRemapper() {
            public void registerMap() {
              handler(wrapper -> {
                    String channel = (String)wrapper.passthrough(Type.STRING);
                    if (channel.length() > 32) {
                      if (!Via.getConfig().isSuppressConversionWarnings())
                        Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel, as it is longer than 32 characters: " + channel); 
                      wrapper.cancel();
                    } else if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                      String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\000");
                      List<String> checkedChannels = new ArrayList<>(channels.length);
                      for (String registeredChannel : channels) {
                        if (registeredChannel.length() > 32) {
                          if (!Via.getConfig().isSuppressConversionWarnings())
                            Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel register of '" + registeredChannel + "', as it is longer than 32 characters"); 
                        } else {
                          checkedChannels.add(registeredChannel);
                        } 
                      } 
                      if (checkedChannels.isEmpty()) {
                        wrapper.cancel();
                        return;
                      } 
                      wrapper.write(Type.REMAINING_BYTES, Joiner.on(false).join(checkedChannels).getBytes(StandardCharsets.UTF_8));
                    } 
                  });
            }
          }); 
    registerServerbound(ServerboundPackets1_16.PLAYER_ABILITIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.BYTE);
                  wrapper.write((Type)Type.FLOAT, Float.valueOf(0.05F));
                  wrapper.write((Type)Type.FLOAT, Float.valueOf(0.1F));
                });
          }
        });
    cancelServerbound(ServerboundPackets1_16.GENERATE_JIGSAW);
    cancelServerbound(ServerboundPackets1_16.UPDATE_JIGSAW_BLOCK);
  }
  
  protected void onMappingDataLoaded() {
    int[] wallPostOverrideTag = new int[47];
    int arrayIndex = 0;
    wallPostOverrideTag[arrayIndex++] = 140;
    wallPostOverrideTag[arrayIndex++] = 179;
    wallPostOverrideTag[arrayIndex++] = 264;
    int i;
    for (i = 153; i <= 158; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    for (i = 163; i <= 168; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    for (i = 408; i <= 439; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wall_post_override", wallPostOverrideTag);
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:beacon_base_blocks", new int[] { 133, 134, 148, 265 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:climbable", new int[] { 160, 241, 658 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fire", new int[] { 142 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:campfires", new int[] { 679 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fence_gates", new int[] { 242, 467, 468, 469, 470, 471 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:unstable_bottom_center", new int[] { 242, 467, 468, 469, 470, 471 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wooden_trapdoors", new int[] { 193, 194, 195, 196, 197, 198 });
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:wooden_trapdoors", new int[] { 215, 216, 217, 218, 219, 220 });
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:beacon_payment_items", new int[] { 529, 530, 531, 760 });
    this.tagRewriter.addTag(RegistryType.ENTITY, "minecraft:impact_projectiles", new int[] { 2, 72, 71, 37, 69, 79, 83, 15, 93 });
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:guarded_by_piglins");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_speed_blocks");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_fire_base_blocks");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:non_flammable_wood");
    this.tagRewriter.addEmptyTag(RegistryType.ITEM, "minecraft:non_flammable_wood");
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { 
          "minecraft:bamboo_plantable_on", "minecraft:beds", "minecraft:bee_growables", "minecraft:beehives", "minecraft:coral_plants", "minecraft:crops", "minecraft:dragon_immune", "minecraft:flowers", "minecraft:portals", "minecraft:shulker_boxes", 
          "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:underwater_bonemeals", "minecraft:wither_immune", "minecraft:wooden_fences", "minecraft:wooden_trapdoors" });
    this.tagRewriter.addEmptyTags(RegistryType.ENTITY, new String[] { "minecraft:arrows", "minecraft:beehive_inhabitors", "minecraft:raiders", "minecraft:skeletons" });
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { 
          "minecraft:beds", "minecraft:coals", "minecraft:fences", "minecraft:flowers", "minecraft:lectern_books", "minecraft:music_discs", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:walls", 
          "minecraft:wooden_fences" });
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(userConnection, (EntityType)Entity1_16Types.PLAYER));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.metadataRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
