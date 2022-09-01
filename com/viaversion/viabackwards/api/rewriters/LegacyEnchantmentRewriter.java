package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LegacyEnchantmentRewriter {
  private final Map<Short, String> enchantmentMappings = new HashMap<>();
  
  private final String nbtTagName;
  
  private Set<Short> hideLevelForEnchants;
  
  public LegacyEnchantmentRewriter(String nbtTagName) {
    this.nbtTagName = nbtTagName;
  }
  
  public void registerEnchantment(int id, String replacementLore) {
    this.enchantmentMappings.put(Short.valueOf((short)id), replacementLore);
  }
  
  public void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnchant) {
    String key = storedEnchant ? "StoredEnchantments" : "ench";
    ListTag enchantments = (ListTag)tag.get(key);
    ListTag remappedEnchantments = new ListTag(CompoundTag.class);
    List<Tag> lore = new ArrayList<>();
    for (Tag enchantmentEntry : enchantments.clone()) {
      Tag idTag = ((CompoundTag)enchantmentEntry).get("id");
      if (idTag == null)
        continue; 
      short newId = ((NumberTag)idTag).asShort();
      String enchantmentName = this.enchantmentMappings.get(Short.valueOf(newId));
      if (enchantmentName != null) {
        enchantments.remove(enchantmentEntry);
        short level = ((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort();
        if (this.hideLevelForEnchants != null && this.hideLevelForEnchants.contains(Short.valueOf(newId))) {
          lore.add(new StringTag(enchantmentName));
        } else {
          lore.add(new StringTag(enchantmentName + " " + EnchantmentRewriter.getRomanNumber(level)));
        } 
        remappedEnchantments.add(enchantmentEntry);
      } 
    } 
    if (!lore.isEmpty()) {
      if (!storedEnchant && enchantments.size() == 0) {
        CompoundTag dummyEnchantment = new CompoundTag();
        dummyEnchantment.put("id", (Tag)new ShortTag((short)0));
        dummyEnchantment.put("lvl", (Tag)new ShortTag((short)0));
        enchantments.add((Tag)dummyEnchantment);
        tag.put(this.nbtTagName + "|dummyEnchant", (Tag)new ByteTag());
        IntTag hideFlags = (IntTag)tag.get("HideFlags");
        if (hideFlags == null) {
          hideFlags = new IntTag();
        } else {
          tag.put(this.nbtTagName + "|oldHideFlags", (Tag)new IntTag(hideFlags.asByte()));
        } 
        int flags = hideFlags.asByte() | 0x1;
        hideFlags.setValue(flags);
        tag.put("HideFlags", (Tag)hideFlags);
      } 
      tag.put(this.nbtTagName + "|" + key, (Tag)remappedEnchantments);
      CompoundTag display = (CompoundTag)tag.get("display");
      if (display == null)
        tag.put("display", (Tag)(display = new CompoundTag())); 
      ListTag loreTag = (ListTag)display.get("Lore");
      if (loreTag == null)
        display.put("Lore", (Tag)(loreTag = new ListTag(StringTag.class))); 
      lore.addAll(loreTag.getValue());
      loreTag.setValue(lore);
    } 
  }
  
  public void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnchant) {
    String key = storedEnchant ? "StoredEnchantments" : "ench";
    ListTag remappedEnchantments = (ListTag)tag.remove(this.nbtTagName + "|" + key);
    ListTag enchantments = (ListTag)tag.get(key);
    if (enchantments == null)
      enchantments = new ListTag(CompoundTag.class); 
    if (!storedEnchant && tag.remove(this.nbtTagName + "|dummyEnchant") != null) {
      for (Tag enchantment : enchantments.clone()) {
        short id = ((NumberTag)((CompoundTag)enchantment).get("id")).asShort();
        short level = ((NumberTag)((CompoundTag)enchantment).get("lvl")).asShort();
        if (id == 0 && level == 0)
          enchantments.remove(enchantment); 
      } 
      IntTag hideFlags = (IntTag)tag.remove(this.nbtTagName + "|oldHideFlags");
      if (hideFlags != null) {
        tag.put("HideFlags", (Tag)new IntTag(hideFlags.asByte()));
      } else {
        tag.remove("HideFlags");
      } 
    } 
    CompoundTag display = (CompoundTag)tag.get("display");
    ListTag lore = (display != null) ? (ListTag)display.get("Lore") : null;
    for (Tag enchantment : remappedEnchantments.clone()) {
      enchantments.add(enchantment);
      if (lore != null && lore.size() != 0)
        lore.remove(lore.get(0)); 
    } 
    if (lore != null && lore.size() == 0) {
      display.remove("Lore");
      if (display.isEmpty())
        tag.remove("display"); 
    } 
    tag.put(key, (Tag)enchantments);
  }
  
  public void setHideLevelForEnchants(int... enchants) {
    this.hideLevelForEnchants = new HashSet<>();
    for (int enchant : enchants)
      this.hideLevelForEnchants.add(Short.valueOf((short)enchant)); 
  }
}
