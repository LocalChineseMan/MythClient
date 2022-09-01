package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viabackwards.api.rewriters.LegacyBlockItemRewriter;
import com.viaversion.viabackwards.api.rewriters.LegacyEnchantmentRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.ChestedHorseStorage;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.WindowTracker;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.Arrays;
import java.util.Optional;

public class BlockItemPackets1_11 extends LegacyBlockItemRewriter<Protocol1_10To1_11> {
  private LegacyEnchantmentRewriter enchantmentRewriter;
  
  public BlockItemPackets1_11(Protocol1_10To1_11 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(BlockItemPackets1_11.this.itemToClientHandler(Type.ITEM));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                      Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                      if (!horse.isPresent())
                        return; 
                      ChestedHorseStorage storage = horse.get();
                      int currentSlot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf(Integer.valueOf(currentSlot = BlockItemPackets1_11.this.getNewSlotId(storage, currentSlot)).shortValue()));
                      wrapper.set(Type.ITEM, 0, BlockItemPackets1_11.this.getNewItem(storage, currentSlot, (Item)wrapper.get(Type.ITEM, 0)));
                    } 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.ITEM_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item[] stacks = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                    for (int i = 0; i < stacks.length; i++)
                      stacks[i] = BlockItemPackets1_11.this.handleItemToClient(stacks[i]); 
                    if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                      Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                      if (!horse.isPresent())
                        return; 
                      ChestedHorseStorage storage = horse.get();
                      stacks = Arrays.<Item>copyOf(stacks, !storage.isChested() ? 38 : 53);
                      for (int j = stacks.length - 1; j >= 0; j--) {
                        stacks[BlockItemPackets1_11.this.getNewSlotId(storage, j)] = stacks[j];
                        stacks[j] = BlockItemPackets1_11.this.getNewItem(storage, j, stacks[j]);
                      } 
                      wrapper.set(Type.ITEM_ARRAY, 0, stacks);
                    } 
                  }
                });
          }
        });
    registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough((Type)Type.INT);
                      int size = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM)));
                        wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM)));
                        boolean secondItem = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM))); 
                        wrapper.passthrough((Type)Type.BOOLEAN);
                        wrapper.passthrough((Type)Type.INT);
                        wrapper.passthrough((Type)Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(Type.ITEM);
            handler(BlockItemPackets1_11.this.itemToServerHandler(Type.ITEM));
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                      Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                      if (!horse.isPresent())
                        return; 
                      ChestedHorseStorage storage = horse.get();
                      int clickSlot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      int correctSlot = BlockItemPackets1_11.this.getOldSlotId(storage, clickSlot);
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf(Integer.valueOf(correctSlot).shortValue()));
                    } 
                  }
                });
          }
        });
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_9_3.CREATIVE_INVENTORY_ACTION, Type.ITEM);
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    BlockItemPackets1_11.this.handleChunk(chunk);
                    for (CompoundTag tag : chunk.getBlockEntities()) {
                      Tag idTag = tag.get("id");
                      if (!(idTag instanceof StringTag))
                        continue; 
                      String id = (String)idTag.getValue();
                      if (id.equals("minecraft:sign"))
                        ((StringTag)idTag).setValue("Sign"); 
                    } 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int idx = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(BlockItemPackets1_11.this.handleBlockID(idx)));
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.MULTI_BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.INT);
            map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (BlockChangeRecord record : (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0))
                      record.setBlockId(BlockItemPackets1_11.this.handleBlockID(record.getBlockId())); 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 10)
                      wrapper.cancel(); 
                    if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 1) {
                      CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                      EntityIdRewriter.toClientSpawner(tag, true);
                    } 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.COMPONENT);
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = -1;
                    if (((String)wrapper.get(Type.STRING, 0)).equals("EntityHorse"))
                      entityId = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue(); 
                    String inventory = (String)wrapper.get(Type.STRING, 0);
                    WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
                    windowTracker.setInventory(inventory);
                    windowTracker.setEntityId(entityId);
                    if (BlockItemPackets1_11.this.isLlama(wrapper.user()))
                      wrapper.set((Type)Type.UNSIGNED_BYTE, 1, Short.valueOf((short)17)); 
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
                    windowTracker.setInventory(null);
                    windowTracker.setEntityId(-1);
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
                    windowTracker.setInventory(null);
                    windowTracker.setEntityId(-1);
                  }
                });
          }
        });
    ((Protocol1_10To1_11)this.protocol).getEntityRewriter().filter().handler((event, meta) -> {
          if (meta.metaType().type().equals(Type.ITEM))
            meta.setValue(handleItemToClient((Item)meta.getValue())); 
        });
  }
  
  protected void registerRewrites() {
    MappedLegacyBlockItem data = (MappedLegacyBlockItem)this.replacementData.computeIfAbsent(52, s -> new MappedLegacyBlockItem(52, (short)-1, null, false));
    data.setBlockEntityHandler((b, tag) -> {
          EntityIdRewriter.toClientSpawner(tag, true);
          return tag;
        });
    this.enchantmentRewriter = new LegacyEnchantmentRewriter(this.nbtTagName);
    this.enchantmentRewriter.registerEnchantment(71, "§cCurse of Vanishing");
    this.enchantmentRewriter.registerEnchantment(10, "§cCurse of Binding");
    this.enchantmentRewriter.setHideLevelForEnchants(new int[] { 71, 10 });
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    super.handleItemToClient(item);
    CompoundTag tag = item.tag();
    if (tag == null)
      return item; 
    EntityIdRewriter.toClientItem(item, true);
    if (tag.get("ench") instanceof com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag)
      this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, false); 
    if (tag.get("StoredEnchantments") instanceof com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag)
      this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, true); 
    return item;
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    super.handleItemToServer(item);
    CompoundTag tag = item.tag();
    if (tag == null)
      return item; 
    EntityIdRewriter.toServerItem(item, true);
    if (tag.contains(this.nbtTagName + "|ench"))
      this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, false); 
    if (tag.contains(this.nbtTagName + "|StoredEnchantments"))
      this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, true); 
    return item;
  }
  
  private boolean isLlama(UserConnection user) {
    WindowTracker tracker = (WindowTracker)user.get(WindowTracker.class);
    if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse")) {
      EntityTracker entTracker = user.getEntityTracker(Protocol1_10To1_11.class);
      StoredEntityData entityData = entTracker.entityData(tracker.getEntityId());
      return (entityData != null && entityData.type().is((EntityType)Entity1_11Types.EntityType.LIAMA));
    } 
    return false;
  }
  
  private Optional<ChestedHorseStorage> getChestedHorse(UserConnection user) {
    WindowTracker tracker = (WindowTracker)user.get(WindowTracker.class);
    if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse")) {
      EntityTracker entTracker = user.getEntityTracker(Protocol1_10To1_11.class);
      StoredEntityData entityData = entTracker.entityData(tracker.getEntityId());
      if (entityData != null)
        return Optional.of((ChestedHorseStorage)entityData.get(ChestedHorseStorage.class)); 
    } 
    return Optional.empty();
  }
  
  private int getNewSlotId(ChestedHorseStorage storage, int slotId) {
    int totalSlots = !storage.isChested() ? 38 : 53;
    int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
    int startNonExistingFormula = 2 + 3 * strength;
    int offsetForm = 15 - 3 * strength;
    if (slotId >= startNonExistingFormula && totalSlots > slotId + offsetForm)
      return offsetForm + slotId; 
    if (slotId == 1)
      return 0; 
    return slotId;
  }
  
  private int getOldSlotId(ChestedHorseStorage storage, int slotId) {
    int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
    int startNonExistingFormula = 2 + 3 * strength;
    int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
    int offsetForm = endNonExistingFormula - startNonExistingFormula;
    if (slotId == 1 || (slotId >= startNonExistingFormula && slotId < endNonExistingFormula))
      return 0; 
    if (slotId >= endNonExistingFormula)
      return slotId - offsetForm; 
    if (slotId == 0)
      return 1; 
    return slotId;
  }
  
  private Item getNewItem(ChestedHorseStorage storage, int slotId, Item current) {
    int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
    int startNonExistingFormula = 2 + 3 * strength;
    int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
    if (slotId >= startNonExistingFormula && slotId < endNonExistingFormula)
      return (Item)new DataItem(166, (byte)1, (short)0, getNamedTag("§4SLOT DISABLED")); 
    if (slotId == 1)
      return null; 
    return current;
  }
}
