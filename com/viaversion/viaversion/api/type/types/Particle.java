package com.viaversion.viaversion.api.type.types;

import java.util.LinkedList;
import java.util.List;

public class Particle {
  private int id;
  
  private List<ParticleData> arguments = new LinkedList<>();
  
  public Particle(int id) {
    this.id = id;
  }
  
  public int getId() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public List<ParticleData> getArguments() {
    return this.arguments;
  }
  
  public void setArguments(List<ParticleData> arguments) {
    this.arguments = arguments;
  }
  
  public static class Particle {}
}
