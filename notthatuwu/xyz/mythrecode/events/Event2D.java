package notthatuwu.xyz.mythrecode.events;

import net.minecraft.client.gui.ScaledResolution;
import notthatuwu.xyz.mythrecode.api.event.Event;

public class Event2D extends Event {
  private final ScaledResolution resolution;
  
  public ScaledResolution getResolution() {
    return this.resolution;
  }
  
  public Event2D(ScaledResolution resolution) {
    this.resolution = resolution;
  }
}
