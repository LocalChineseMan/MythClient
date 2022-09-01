package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class Event3D extends Event {
  private final float partialTicks;
  
  public float getPartialTicks() {
    return this.partialTicks;
  }
  
  public Event3D(float partialTicks) {
    this.partialTicks = partialTicks;
  }
}
