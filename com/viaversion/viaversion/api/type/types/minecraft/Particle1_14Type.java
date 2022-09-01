package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;

public class Particle1_14Type extends AbstractParticleType {
  public Particle1_14Type() {
    this.readers.put(3, blockHandler());
    this.readers.put(23, blockHandler());
    this.readers.put(14, dustHandler());
    this.readers.put(32, itemHandler(Type.FLAT_VAR_INT_ITEM));
  }
}
