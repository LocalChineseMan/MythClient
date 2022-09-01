package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;

public class Particle1_17Type extends AbstractParticleType {
  public Particle1_17Type() {
    this.readers.put(4, blockHandler());
    this.readers.put(25, blockHandler());
    this.readers.put(15, dustHandler());
    this.readers.put(16, dustTransitionHandler());
    this.readers.put(36, itemHandler(Type.FLAT_VAR_INT_ITEM));
    this.readers.put(37, vibrationHandler(Type.POSITION1_14));
  }
}
