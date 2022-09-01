package com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public class InventoryPackets extends ItemRewriter<Protocol1_16_2To1_16_1> {
  public InventoryPackets(Protocol1_16_2To1_16_1 protocol) {
    super((Protocol)protocol);
  }
  
  public void registerPackets() {
    registerSetCooldown((ClientboundPacketType)ClientboundPackets1_16.COOLDOWN);
    registerWindowItems((ClientboundPacketType)ClientboundPackets1_16.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    registerTradeList((ClientboundPacketType)ClientboundPackets1_16.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    registerSetSlot((ClientboundPacketType)ClientboundPackets1_16.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    registerEntityEquipmentArray((ClientboundPacketType)ClientboundPackets1_16.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    registerAdvancements((ClientboundPacketType)ClientboundPackets1_16.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_16_2To1_16_1)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.UNLOCK_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(false));
                });
          }
        });
    (new RecipeRewriter1_16(this.protocol)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_16.DECLARE_RECIPES);
    registerClickWindow((ServerboundPacketType)ServerboundPackets1_16_2.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16_2.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    ((Protocol1_16_2To1_16_1)this.protocol).registerServerbound((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.this.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.DOUBLE);
  }
}
