package notthatuwu.xyz.mythrecode.modules.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;

@Info(name = "Velocity", description = "Basically anti kb lol", category = Category.COMBAT)
public class Velocity extends Module {
  public final ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Vanilla", "Redesky" }, "Cancel");
  
  public final NumberSetting vertical = new NumberSetting("Vertical", this, 1.0D, 0.0D, 100.0D, true, () -> Boolean.valueOf(this.mode.is("Vanilla")));
  
  public final NumberSetting horizontal = new NumberSetting("Horizontal", this, 1.0D, 0.0D, 100.0D, true, () -> Boolean.valueOf(this.mode.is("Vanilla")));
  
  @EventTarget
  public void onReceivePacket(EventReceivePacket event) {
    setSuffix(this.mode.getValue());
    if (event.getPacket() instanceof S12PacketEntityVelocity) {
      S12PacketEntityVelocity s12PacketEntityVelocity;
      switch (this.mode.getValue()) {
        case "Vanilla":
          if (event.getPacket() instanceof S12PacketEntityVelocity && (
            (S12PacketEntityVelocity)event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
            if (this.vertical.getValue().doubleValue() == 0.0D && this.horizontal.getValue().doubleValue() == 0.0D) {
              event.setCancelled(true);
            } else {
              packet.motionY = (int)(packet.motionY * this.vertical.getValue().doubleValue() / 100.0D);
              packet.motionX = (int)(packet.motionX * this.horizontal.getValue().doubleValue() / 100.0D);
              packet.motionZ = (int)(packet.motionZ * this.horizontal.getValue().doubleValue() / 100.0D);
            } 
          } 
          if (event.getPacket() instanceof net.minecraft.network.play.server.S27PacketExplosion)
            event.setCancelled(true); 
          break;
        case "Redesky":
          s12PacketEntityVelocity = (S12PacketEntityVelocity)event.getPacket();
          if (((KillAura)Client.INSTANCE.moduleManager.getModuleByClass(KillAura.class)).isEnabled() && KillAura.target != null && mc.thePlayer.motionY > -0.8D) {
            s12PacketEntityVelocity.setMotionX(0);
            s12PacketEntityVelocity.setMotionZ(0);
            s12PacketEntityVelocity.setMotionY((int)(s12PacketEntityVelocity.motionY * 0.5F));
          } 
          break;
      } 
    } 
  }
}
