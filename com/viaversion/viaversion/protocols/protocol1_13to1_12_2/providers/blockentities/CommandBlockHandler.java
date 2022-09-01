package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;

public class CommandBlockHandler implements BlockEntityProvider.BlockEntityHandler {
  public int transform(UserConnection user, CompoundTag tag) {
    Tag name = tag.get("CustomName");
    if (name instanceof StringTag)
      ((StringTag)name).setValue(ChatRewriter.legacyTextToJsonString(((StringTag)name).getValue())); 
    Tag out = tag.get("LastOutput");
    if (out instanceof StringTag) {
      JsonElement value = JsonParser.parseString(((StringTag)out).getValue());
      ChatRewriter.processTranslate(value);
      ((StringTag)out).setValue(value.toString());
    } 
    return -1;
  }
}
