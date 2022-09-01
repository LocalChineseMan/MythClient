package com.viaversion.viaversion.libs.opennbt.tag;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class TagRegistry {
  private static final Int2ObjectMap<Class<? extends Tag>> idToTag = (Int2ObjectMap<Class<? extends Tag>>)new Int2ObjectOpenHashMap();
  
  private static final Object2IntMap<Class<? extends Tag>> tagToId = (Object2IntMap<Class<? extends Tag>>)new Object2IntOpenHashMap();
  
  private static final Int2ObjectMap<Supplier<? extends Tag>> instanceSuppliers = (Int2ObjectMap<Supplier<? extends Tag>>)new Int2ObjectOpenHashMap();
  
  static {
    tagToId.defaultReturnValue(-1);
    register(1, (Class)ByteTag.class, ByteTag::new);
    register(2, (Class)ShortTag.class, ShortTag::new);
    register(3, (Class)IntTag.class, IntTag::new);
    register(4, (Class)LongTag.class, LongTag::new);
    register(5, (Class)FloatTag.class, FloatTag::new);
    register(6, (Class)DoubleTag.class, DoubleTag::new);
    register(7, (Class)ByteArrayTag.class, ByteArrayTag::new);
    register(8, (Class)StringTag.class, StringTag::new);
    register(9, (Class)ListTag.class, ListTag::new);
    register(10, (Class)CompoundTag.class, CompoundTag::new);
    register(11, (Class)IntArrayTag.class, IntArrayTag::new);
    register(12, (Class)LongArrayTag.class, LongArrayTag::new);
  }
  
  public static void register(int id, Class<? extends Tag> tag, Supplier<? extends Tag> supplier) throws TagRegisterException {
    if (idToTag.containsKey(id))
      throw new TagRegisterException("Tag ID \"" + id + "\" is already in use."); 
    if (tagToId.containsKey(tag))
      throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered."); 
    instanceSuppliers.put(id, supplier);
    idToTag.put(id, tag);
    tagToId.put(tag, id);
  }
  
  public static void unregister(int id) {
    tagToId.removeInt(getClassFor(id));
    idToTag.remove(id);
  }
  
  @Nullable
  public static Class<? extends Tag> getClassFor(int id) {
    return (Class<? extends Tag>)idToTag.get(id);
  }
  
  public static int getIdFor(Class<? extends Tag> clazz) {
    return tagToId.getInt(clazz);
  }
  
  public static Tag createInstance(int id) throws TagCreateException {
    Supplier<? extends Tag> supplier = (Supplier<? extends Tag>)instanceSuppliers.get(id);
    if (supplier == null)
      throw new TagCreateException("Could not find tag with ID \"" + id + "\"."); 
    return supplier.get();
  }
}
