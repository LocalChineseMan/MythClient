package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public abstract class ItemRewriterBase<T extends BackwardsProtocol> extends ItemRewriter<T> {
  protected final String nbtTagName;
  
  protected final boolean jsonNameFormat;
  
  protected ItemRewriterBase(T protocol, boolean jsonNameFormat) {
    super((Protocol)protocol);
    this.jsonNameFormat = jsonNameFormat;
    this.nbtTagName = "VB|" + protocol.getClass().getSimpleName();
  }
  
  public Item handleItemToServer(Item item) {
    if (item == null)
      return null; 
    super.handleItemToServer(item);
    restoreDisplayTag(item);
    return item;
  }
  
  protected boolean hasBackupTag(CompoundTag displayTag, String tagName) {
    return displayTag.contains(this.nbtTagName + "|o" + tagName);
  }
  
  protected void saveStringTag(CompoundTag displayTag, StringTag original, String name) {
    String backupName = this.nbtTagName + "|o" + name;
    if (!displayTag.contains(backupName))
      displayTag.put(backupName, (Tag)new StringTag(original.getValue())); 
  }
  
  protected void saveListTag(CompoundTag displayTag, ListTag original, String name) {
    String backupName = this.nbtTagName + "|o" + name;
    if (!displayTag.contains(backupName)) {
      ListTag listTag = new ListTag();
      for (Tag tag : original.getValue())
        listTag.add(tag.clone()); 
      displayTag.put(backupName, (Tag)listTag);
    } 
  }
  
  protected void restoreDisplayTag(Item item) {
    if (item.tag() == null)
      return; 
    CompoundTag display = (CompoundTag)item.tag().get("display");
    if (display != null) {
      if (display.remove(this.nbtTagName + "|customName") != null) {
        display.remove("Name");
      } else {
        restoreStringTag(display, "Name");
      } 
      restoreListTag(display, "Lore");
    } 
  }
  
  protected void restoreStringTag(CompoundTag tag, String tagName) {
    StringTag original = (StringTag)tag.remove(this.nbtTagName + "|o" + tagName);
    if (original != null)
      tag.put(tagName, (Tag)new StringTag(original.getValue())); 
  }
  
  protected void restoreListTag(CompoundTag tag, String tagName) {
    ListTag original = (ListTag)tag.remove(this.nbtTagName + "|o" + tagName);
    if (original != null)
      tag.put(tagName, (Tag)new ListTag(original.getValue())); 
  }
  
  public String getNbtTagName() {
    return this.nbtTagName;
  }
}
