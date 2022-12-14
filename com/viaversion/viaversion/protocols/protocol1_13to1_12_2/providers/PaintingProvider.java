package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers;

import com.viaversion.viaversion.api.platform.providers.Provider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PaintingProvider implements Provider {
  private final Map<String, Integer> paintings = new HashMap<>();
  
  public PaintingProvider() {
    add("kebab");
    add("aztec");
    add("alban");
    add("aztec2");
    add("bomb");
    add("plant");
    add("wasteland");
    add("pool");
    add("courbet");
    add("sea");
    add("sunset");
    add("creebet");
    add("wanderer");
    add("graham");
    add("match");
    add("bust");
    add("stage");
    add("void");
    add("skullandroses");
    add("wither");
    add("fighters");
    add("pointer");
    add("pigscene");
    add("burningskull");
    add("skeleton");
    add("donkeykong");
  }
  
  private void add(String motive) {
    this.paintings.put("minecraft:" + motive, Integer.valueOf(this.paintings.size()));
  }
  
  public Optional<Integer> getIntByIdentifier(String motive) {
    if (!motive.startsWith("minecraft:"))
      motive = "minecraft:" + motive.toLowerCase(Locale.ROOT); 
    return Optional.ofNullable(this.paintings.get(motive));
  }
}
