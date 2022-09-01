package notthatuwu.xyz.mythrecode.api.ui.notifications;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationManager {
  public ArrayList<Notification> notifications = new ArrayList<>();
  
  public void sendNotification(String message, String title) {
    if (this.notifications.size() > 8)
      this.notifications.remove(0); 
    boolean has = false;
    for (Notification n : this.notifications) {
      if (n.getMessage().equals(message))
        has = true; 
    } 
    if (!has)
      this.notifications.add(new Notification(message, title)); 
  }
  
  public void drawNotifications() {
    try {
      ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
      double startY = (res.getScaledHeight() - 25);
      double lastY = startY;
      for (int i = 0; i < this.notifications.size(); i++) {
        Notification not = this.notifications.get(i);
        if (not.canRemove())
          this.notifications.remove(i); 
        not.draw(startY);
        startY -= not.getHeight() + 1.0D;
      } 
    } catch (Throwable throwable) {}
  }
}
