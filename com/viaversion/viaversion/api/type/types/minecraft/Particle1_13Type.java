package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;

public class Particle1_13Type extends AbstractParticleType {
  public Particle1_13Type() {
    this.readers.put(3, blockHandler());
    this.readers.put(20, blockHandler());
    this.readers.put(11, dustHandler());
    this.readers.put(27, itemHandler(Type.FLAT_ITEM));
  }
}
