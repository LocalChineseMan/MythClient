package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.BlockColors;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import java.util.HashMap;
import java.util.Map;

public abstract class LegacyBlockItemRewriter<T extends BackwardsProtocol> extends ItemRewriterBase<T> {
  private static final Map<String, Int2ObjectMap<MappedLegacyBlockItem>> LEGACY_MAPPINGS = new HashMap<>();
  
  protected final Int2ObjectMap<MappedLegacyBlockItem> replacementData;
  
  static {
    JsonObject jsonObject = VBMappingDataLoader.loadFromDataDir("legacy-mappings.json");
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)jsonObject.entrySet()) {
      Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap(8);
      LEGACY_MAPPINGS.put(entry.getKey(), int2ObjectOpenHashMap);
      for (Map.Entry<String, JsonElement> dataEntry : (Iterable<Map.Entry<String, JsonElement>>)((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
        JsonObject object = ((JsonElement)dataEntry.getValue()).getAsJsonObject();
        int id = object.getAsJsonPrimitive("id").getAsInt();
        JsonPrimitive jsonData = object.getAsJsonPrimitive("data");
        short data = (jsonData != null) ? jsonData.getAsShort() : 0;
        String name = object.getAsJsonPrimitive("name").getAsString();
        JsonPrimitive blockField = object.getAsJsonPrimitive("block");
        boolean block = (blockField != null && blockField.getAsBoolean());
        if (((String)dataEntry.getKey()).indexOf('-') != -1) {
          String[] split = ((String)dataEntry.getKey()).split("-", 2);
          int from = Integer.parseInt(split[0]);
          int to = Integer.parseInt(split[1]);
          if (name.contains("%color%")) {
            for (int j = from; j <= to; j++)
              int2ObjectOpenHashMap.put(j, new MappedLegacyBlockItem(id, data, name.replace("%color%", BlockColors.get(j - from)), block)); 
            continue;
          } 
          MappedLegacyBlockItem mappedBlockItem = new MappedLegacyBlockItem(id, data, name, block);
          for (int i = from; i <= to; i++)
            int2ObjectOpenHashMap.put(i, mappedBlockItem); 
          continue;
        } 
        int2ObjectOpenHashMap.put(Integer.parseInt(dataEntry.getKey()), new MappedLegacyBlockItem(id, data, name, block));
      } 
    } 
  }
  
  protected LegacyBlockItemRewriter(T protocol) {
    super(protocol, false);
    this.replacementData = LEGACY_MAPPINGS.get(protocol.getClass().getSimpleName().split("To")[1].replace("_", "."));
  }
  
  public Item handleItemToClient(Item item) {
    if (item == null)
      return null; 
    MappedLegacyBlockItem data = (MappedLegacyBlockItem)this.replacementData.get(item.identifier());
    if (data == null)
      return super.handleItemToClient(item); 
    short originalData = item.data();
    item.setIdentifier(data.getId());
    if (data.getData() != -1)
      item.setData(data.getData()); 
    if (data.getName() != null) {
      if (item.tag() == null)
        item.setTag(new CompoundTag()); 
      CompoundTag display = (CompoundTag)item.tag().get("display");
      if (display == null)
        item.tag().put("display", (Tag)(display = new CompoundTag())); 
      StringTag nameTag = (StringTag)display.get("Name");
      if (nameTag == null) {
        display.put("Name", (Tag)(nameTag = new StringTag(data.getName())));
        display.put(this.nbtTagName + "|customName", (Tag)new ByteTag());
      } 
      String value = nameTag.getValue();
      if (value.contains("%vb_color%"))
        display.put("Name", (Tag)new StringTag(value.replace("%vb_color%", BlockColors.get(originalData)))); 
    } 
    return item;
  }
  
  public int handleBlockID(int idx) {
    int type = idx >> 4;
    int meta = idx & 0xF;
    Block b = handleBlock(type, meta);
    if (b == null)
      return idx; 
    return b.getId() << 4 | b.getData() & 0xF;
  }
  
  public Block handleBlock(int blockId, int data) {
    MappedLegacyBlockItem settings = (MappedLegacyBlockItem)this.replacementData.get(blockId);
    if (settings == null || !settings.isBlock())
      return null; 
    Block block = settings.getBlock();
    if (block.getData() == -1)
      return block.withData(data); 
    return block;
  }
  
  protected void handleChunk(Chunk chunk) {
    Map<Pos, CompoundTag> tags = new HashMap<>();
    for (CompoundTag tag : chunk.getBlockEntities()) {
      Tag xTag, yTag, zTag;
      if ((xTag = tag.get("x")) == null || (yTag = tag.get("y")) == null || (zTag = tag.get("z")) == null)
        continue; 
      Pos pos = new Pos(((NumberTag)xTag).asInt() & 0xF, ((NumberTag)yTag).asInt(), ((NumberTag)zTag).asInt() & 0xF, null);
      tags.put(pos, tag);
      if (pos.getY() < 0 || pos.getY() > 255)
        continue; 
      ChunkSection section = chunk.getSections()[pos.getY() >> 4];
      if (section == null)
        continue; 
      int block = section.getFlatBlock(pos.getX(), pos.getY() & 0xF, pos.getZ());
      int btype = block >> 4;
      MappedLegacyBlockItem settings = (MappedLegacyBlockItem)this.replacementData.get(btype);
      if (settings != null && settings.hasBlockEntityHandler())
        settings.getBlockEntityHandler().handleOrNewCompoundTag(block, tag); 
    } 
    for (int i = 0; i < (chunk.getSections()).length; i++) {
      ChunkSection section = chunk.getSections()[i];
      if (section != null) {
        boolean hasBlockEntityHandler = false;
        for (int j = 0; j < section.getPaletteSize(); j++) {
          int block = section.getPaletteEntry(j);
          int btype = block >> 4;
          int meta = block & 0xF;
          Block b = handleBlock(btype, meta);
          if (b != null)
            section.setPaletteEntry(j, b.getId() << 4 | b.getData() & 0xF); 
          if (!hasBlockEntityHandler) {
            MappedLegacyBlockItem settings = (MappedLegacyBlockItem)this.replacementData.get(btype);
            if (settings != null && settings.hasBlockEntityHandler())
              hasBlockEntityHandler = true; 
          } 
        } 
        if (hasBlockEntityHandler)
          for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
              for (int z = 0; z < 16; z++) {
                int block = section.getFlatBlock(x, y, z);
                int btype = block >> 4;
                int meta = block & 0xF;
                MappedLegacyBlockItem settings = (MappedLegacyBlockItem)this.replacementData.get(btype);
                if (settings != null && settings.hasBlockEntityHandler()) {
                  Pos pos = new Pos(x, y + (i << 4), z, null);
                  if (!tags.containsKey(pos)) {
                    CompoundTag tag = new CompoundTag();
                    tag.put("x", (Tag)new IntTag(x + (chunk.getX() << 4)));
                    tag.put("y", (Tag)new IntTag(y + (i << 4)));
                    tag.put("z", (Tag)new IntTag(z + (chunk.getZ() << 4)));
                    settings.getBlockEntityHandler().handleOrNewCompoundTag(block, tag);
                    chunk.getBlockEntities().add(tag);
                  } 
                } 
              } 
            } 
          }  
      } 
    } 
  }
  
  protected CompoundTag getNamedTag(String text) {
    CompoundTag tag = new CompoundTag();
    tag.put("display", (Tag)new CompoundTag());
    text = "§r" + text;
    ((CompoundTag)tag.get("display")).put("Name", (Tag)new StringTag(this.jsonNameFormat ? ChatRewriter.legacyTextToJsonString(text) : text));
    return tag;
  }
  
  private static final class LegacyBlockItemRewriter {}
}
