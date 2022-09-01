package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.legacyimpl;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.BinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.CompoundBinaryTag;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.TagStringIO;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.api.BinaryTagHolder;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.util.Codec;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

final class NBTLegacyHoverEventSerializerImpl implements LegacyHoverEventSerializer {
  static final NBTLegacyHoverEventSerializerImpl INSTANCE = new NBTLegacyHoverEventSerializerImpl();
  
  private static final TagStringIO SNBT_IO = TagStringIO.get();
  
  private static final Codec<CompoundBinaryTag, String, IOException, IOException> SNBT_CODEC = Codec.of(SNBT_IO::asCompound, SNBT_IO::asString);
  
  static final String ITEM_TYPE = "id";
  
  static final String ITEM_COUNT = "Count";
  
  static final String ITEM_TAG = "tag";
  
  static final String ENTITY_NAME = "name";
  
  static final String ENTITY_TYPE = "type";
  
  static final String ENTITY_ID = "id";
  
  static {
    Objects.requireNonNull(SNBT_IO);
    Objects.requireNonNull(SNBT_IO);
  }
  
  public HoverEvent.ShowItem deserializeShowItem(@NotNull Component input) throws IOException {
    assertTextComponent(input);
    CompoundBinaryTag contents = (CompoundBinaryTag)SNBT_CODEC.decode(((TextComponent)input).content());
    CompoundBinaryTag tag = contents.getCompound("tag");
    return HoverEvent.ShowItem.of(
        Key.key(contents.getString("id")), contents
        .getByte("Count", (byte)1), 
        (tag == CompoundBinaryTag.empty()) ? null : BinaryTagHolder.encode(tag, SNBT_CODEC));
  }
  
  public HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
    assertTextComponent(input);
    CompoundBinaryTag contents = (CompoundBinaryTag)SNBT_CODEC.decode(((TextComponent)input).content());
    return HoverEvent.ShowEntity.of(
        Key.key(contents.getString("type")), 
        UUID.fromString(contents.getString("id")), (Component)componentCodec
        .decode(contents.getString("name")));
  }
  
  private static void assertTextComponent(Component component) {
    if (!(component instanceof TextComponent) || !component.children().isEmpty())
      throw new IllegalArgumentException("Legacy events must be single Component instances"); 
  }
  
  @NotNull
  public Component serializeShowItem(HoverEvent.ShowItem input) throws IOException {
    CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.item().asString())).putByte("Count", (byte)input.count());
    BinaryTagHolder nbt = input.nbt();
    if (nbt != null)
      builder.put("tag", (BinaryTag)nbt.get(SNBT_CODEC)); 
    return (Component)Component.text((String)SNBT_CODEC.encode(builder.build()));
  }
  
  @NotNull
  public Component serializeShowEntity(HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
    CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.id().toString())).putString("type", input.type().asString());
    Component name = input.name();
    if (name != null)
      builder.putString("name", (String)componentCodec.encode(name)); 
    return (Component)Component.text((String)SNBT_CODEC.encode(builder.build()));
  }
}
