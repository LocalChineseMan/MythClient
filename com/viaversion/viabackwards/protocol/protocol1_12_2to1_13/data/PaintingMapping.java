package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;

public class PaintingMapping {
  private static final Int2ObjectMap<String> PAINTINGS = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap(26, 1.0F);
  
  public static void init() {
    add("Kebab");
    add("Aztec");
    add("Alban");
    add("Aztec2");
    add("Bomb");
    add("Plant");
    add("Wasteland");
    add("Pool");
    add("Courbet");
    add("Sea");
    add("Sunset");
    add("Creebet");
    add("Wanderer");
    add("Graham");
    add("Match");
    add("Bust");
    add("Stage");
    add("Void");
    add("SkullAndRoses");
    add("Wither");
    add("Fighters");
    add("Pointer");
    add("Pigscene");
    add("BurningSkull");
    add("Skeleton");
    add("DonkeyKong");
  }
  
  private static void add(String motive) {
    PAINTINGS.put(PAINTINGS.size(), motive);
  }
  
  public static String getStringId(int id) {
    return (String)PAINTINGS.getOrDefault(id, "kebab");
  }
}
