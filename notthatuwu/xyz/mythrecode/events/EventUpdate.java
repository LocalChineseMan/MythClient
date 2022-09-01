package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public final class EventUpdate extends Event {
  private double x;
  
  private double y;
  
  private double z;
  
  private double lastX;
  
  private double lastY;
  
  private double lastZ;
  
  private float lastYaw;
  
  private float lastPitch;
  
  private boolean onGround;
  
  private boolean rotatingPitch;
  
  private boolean rotatingYaw;
  
  private Type type;
  
  private float yaw;
  
  private float pitch;
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public double getLastX() {
    return this.lastX;
  }
  
  public double getLastY() {
    return this.lastY;
  }
  
  public double getLastZ() {
    return this.lastZ;
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public void setZ(double z) {
    this.z = z;
  }
  
  public void setLastX(double lastX) {
    this.lastX = lastX;
  }
  
  public void setLastY(double lastY) {
    this.lastY = lastY;
  }
  
  public void setLastZ(double lastZ) {
    this.lastZ = lastZ;
  }
  
  public float getLastYaw() {
    return this.lastYaw;
  }
  
  public float getLastPitch() {
    return this.lastPitch;
  }
  
  public void setLastYaw(float lastYaw) {
    this.lastYaw = lastYaw;
  }
  
  public void setLastPitch(float lastPitch) {
    this.lastPitch = lastPitch;
  }
  
  public boolean isOnGround() {
    return this.onGround;
  }
  
  public boolean isRotatingPitch() {
    return this.rotatingPitch;
  }
  
  public boolean isRotatingYaw() {
    return this.rotatingYaw;
  }
  
  public void setOnGround(boolean onGround) {
    this.onGround = onGround;
  }
  
  public void setRotatingPitch(boolean rotatingPitch) {
    this.rotatingPitch = rotatingPitch;
  }
  
  public void setRotatingYaw(boolean rotatingYaw) {
    this.rotatingYaw = rotatingYaw;
  }
  
  public Type getType() {
    return this.type;
  }
  
  public void setType(Type type) {
    this.type = type;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public float getPitch() {
    return this.pitch;
  }
  
  public EventUpdate(double X, double Y, double Z, float yaw, float pitch, boolean onGround, Type type) {
    this.x = X;
    this.y = Y;
    this.z = Z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.onGround = onGround;
    this.type = type;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
    this.rotatingYaw = true;
  }
  
  public void setPitch(float pitch) {
    this.pitch = pitch;
    this.rotatingPitch = true;
  }
  
  public boolean isPre() {
    return (this.type == Type.PRE);
  }
  
  public boolean isPost() {
    return (this.type == Type.POST);
  }
  
  public enum Type {
    PRE, POST;
  }
}
