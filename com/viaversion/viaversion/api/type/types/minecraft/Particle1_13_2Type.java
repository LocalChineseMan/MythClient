package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import io.netty.buffer.ByteBuf;

public class Particle1_13_2Type extends Type<Particle> {
  public Particle1_13_2Type() {
    super("Particle", Particle.class);
  }
  
  public void write(ByteBuf buffer, Particle object) throws Exception {
    Type.VAR_INT.writePrimitive(buffer, object.getId());
    for (Particle.ParticleData data : object.getArguments())
      data.getType().write(buffer, data.getValue()); 
  }
  
  public Particle read(ByteBuf buffer) throws Exception {
    int type = Type.VAR_INT.readPrimitive(buffer);
    Particle particle = new Particle(type);
    switch (type) {
      case 3:
      case 20:
        particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(Type.VAR_INT.readPrimitive(buffer))));
        break;
      case 11:
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        break;
      case 27:
        particle.getArguments().add(new Particle.ParticleData(Type.FLAT_VAR_INT_ITEM, Type.FLAT_VAR_INT_ITEM.read(buffer)));
        break;
    } 
    return particle;
  }
}
