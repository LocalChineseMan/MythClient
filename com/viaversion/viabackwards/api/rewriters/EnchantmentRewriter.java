package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EnchantmentRewriter {
  private final Map<String, String> enchantmentMappings = new HashMap<>();
  
  private final ItemRewriter itemRewriter;
  
  private final boolean jsonFormat;
  
  public EnchantmentRewriter(ItemRewriter itemRewriter, boolean jsonFormat) {
    this.itemRewriter = itemRewriter;
    this.jsonFormat = jsonFormat;
  }
  
  public EnchantmentRewriter(ItemRewriter itemRewriter) {
    this(itemRewriter, true);
  }
  
  public void registerEnchantment(String key, String replacementLore) {
    this.enchantmentMappings.put(key, replacementLore);
  }
  
  public void handleToClient(Item item) {
    CompoundTag tag = item.tag();
    if (tag == null)
      return; 
    if (tag.get("Enchantments") instanceof ListTag)
      rewriteEnchantmentsToClient(tag, false); 
    if (tag.get("StoredEnchantments") instanceof ListTag)
      rewriteEnchantmentsToClient(tag, true); 
  }
  
  public void handleToServer(Item item) {
    CompoundTag tag = item.tag();
    if (tag == null)
      return; 
    if (tag.contains(this.itemRewriter.getNbtTagName() + "|Enchantments"))
      rewriteEnchantmentsToServer(tag, false); 
    if (tag.contains(this.itemRewriter.getNbtTagName() + "|StoredEnchantments"))
      rewriteEnchantmentsToServer(tag, true); 
  }
  
  public void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnchant) {
    String key = storedEnchant ? "StoredEnchantments" : "Enchantments";
    ListTag enchantments = (ListTag)tag.get(key);
    List<Tag> loreToAdd = new ArrayList<>();
    boolean changed = false;
    Iterator<Tag> iterator = enchantments.iterator();
    while (iterator.hasNext()) {
      CompoundTag enchantmentEntry = (CompoundTag)iterator.next();
      StringTag idTag = (StringTag)enchantmentEntry.get("id");
      if (idTag == null)
        continue; 
      String enchantmentId = idTag.getValue();
      String remappedName = this.enchantmentMappings.get(enchantmentId);
      if (remappedName != null) {
        if (!changed) {
          this.itemRewriter.saveListTag(tag, enchantments, key);
          changed = true;
        } 
        iterator.remove();
        int level = ((NumberTag)enchantmentEntry.get("lvl")).asInt();
        String loreValue = remappedName + " " + getRomanNumber(level);
        if (this.jsonFormat)
          loreValue = ChatRewriter.legacyTextToJsonString(loreValue); 
        loreToAdd.add(new StringTag(loreValue));
      } 
    } 
    if (!loreToAdd.isEmpty()) {
      if (!storedEnchant && enchantments.size() == 0) {
        CompoundTag dummyEnchantment = new CompoundTag();
        dummyEnchantment.put("id", (Tag)new StringTag());
        dummyEnchantment.put("lvl", (Tag)new ShortTag((short)0));
        enchantments.add((Tag)dummyEnchantment);
      } 
      CompoundTag display = (CompoundTag)tag.get("display");
      if (display == null)
        tag.put("display", (Tag)(display = new CompoundTag())); 
      ListTag loreTag = (ListTag)display.get("Lore");
      if (loreTag == null) {
        display.put("Lore", (Tag)(loreTag = new ListTag(StringTag.class)));
      } else {
        this.itemRewriter.saveListTag(display, loreTag, "Lore");
      } 
      loreToAdd.addAll(loreTag.getValue());
      loreTag.setValue(loreToAdd);
    } 
  }
  
  public void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnchant) {
    String key = storedEnchant ? "StoredEnchantments" : "Enchantments";
    this.itemRewriter.restoreListTag(tag, key);
  }
  
  public static String getRomanNumber(int number) {
    switch (number) {
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      case 6:
        return "VI";
      case 7:
        return "VII";
      case 8:
        return "VIII";
      case 9:
        return "IX";
      case 10:
        return "X";
    } 
    return Integer.toString(number);
  }
}
