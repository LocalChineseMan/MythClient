package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import io.netty.buffer.ByteBuf;

public abstract class AbstractParticleType extends Type<Particle> {
  protected final Int2ObjectMap<ParticleReader> readers = (Int2ObjectMap<ParticleReader>)new Int2ObjectOpenHashMap();
  
  protected AbstractParticleType() {
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
    ParticleReader reader = (ParticleReader)this.readers.get(type);
    if (reader != null)
      reader.read(buffer, particle); 
    return particle;
  }
  
  protected ParticleReader blockHandler() {
    return (buf, particle) -> particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(Type.VAR_INT.readPrimitive(buf))));
  }
  
  protected ParticleReader itemHandler(Type<Item> itemType) {
    return (buf, particle) -> particle.getArguments().add(new Particle.ParticleData(itemType, itemType.read(buf)));
  }
  
  protected ParticleReader dustHandler() {
    return (buf, particle) -> {
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
      };
  }
  
  protected ParticleReader dustTransitionHandler() {
    return (buf, particle) -> {
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buf))));
      };
  }
  
  protected ParticleReader vibrationHandler(Type<Position> positionType) {
    return (buf, particle) -> {
        particle.getArguments().add(new Particle.ParticleData(positionType, positionType.read(buf)));
        String resourceLocation = (String)Type.STRING.read(buf);
        if (resourceLocation.equals("block")) {
          particle.getArguments().add(new Particle.ParticleData(positionType, positionType.read(buf)));
        } else if (resourceLocation.equals("entity")) {
          particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(Type.VAR_INT.readPrimitive(buf))));
        } else {
          Via.getPlatform().getLogger().warning("Unknown vibration path position source type: " + resourceLocation);
        } 
        particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(Type.VAR_INT.readPrimitive(buf))));
      };
  }
  
  @FunctionalInterface
  public static interface ParticleReader {
    void read(ByteBuf param1ByteBuf, Particle param1Particle) throws Exception;
  }
}
