package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.Event3D;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import org.lwjgl.opengl.GL11;

@Info(name = "JumpCircle", category = Category.VISUAL)
public class JumpCircle extends Module {
  public NumberSetting width = new NumberSetting("Width", (Module)this, 2.0D, 0.1D, 5.0D, false);
  
  public NumberSetting radius = new NumberSetting("Radius", (Module)this, 1.5D, 0.5D, 5.0D, false);
  
  public BooleanSetting onlySelf = new BooleanSetting("Only Self", (Module)this, true);
  
  public BooleanSetting alphaFading = new BooleanSetting("Alpha Fading", (Module)this, true);
  
  public BooleanSetting depth = new BooleanSetting("Depth", (Module)this, true);
  
  public ColorSetting colorSetting = new ColorSetting("Color", (Module)this, Color.RED);
  
  final HashMap<EntityPlayer, Boolean> wasAir = new HashMap<>();
  
  final ArrayList<Circle> circles = new ArrayList<>();
  
  final CopyOnWriteArrayList<Circle> circlesCopy = new CopyOnWriteArrayList<>();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    for (EntityPlayer player : mc.theWorld.playerEntities) {
      if (player == mc.thePlayer || !this.onlySelf.getValue().booleanValue()) {
        if (player.onGround) {
          if (this.wasAir.containsKey(player) && ((Boolean)this.wasAir.get(player)).booleanValue()) {
            this.wasAir.put(player, Boolean.valueOf(false));
            this.circles.add(new Circle(player.getPositionVector(), 0.0D));
          } 
          continue;
        } 
        this.wasAir.put(player, Boolean.valueOf(true));
      } 
    } 
  }
  
  @EventTarget
  public void onRender3D(Event3D event) {
    this.circlesCopy.clear();
    this.circlesCopy.addAll(this.circles);
    for (Circle circle : this.circlesCopy) {
      Vec3 pos = circle.getPosition();
      mc.getRenderManager();
      double y = pos.yCoord - RenderManager.renderPosY;
      circle.adjust(0.02D);
      GL11.glPushMatrix();
      if (Circle.access$000(circle) <= this.radius.getValue().doubleValue()) {
        if (this.depth.getValue().booleanValue())
          GL11.glDisable(2929); 
        GL11.glDisable(3553);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        float percent = 100.0F - (float)(circle.getRadius() * 100.0D / this.radius.getValue().doubleValue());
        Color color = new Color(this.colorSetting.getColor());
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, this.alphaFading.getValue().booleanValue() ? (percent / 100.0F) : 1.0F);
        GL11.glLineWidth(this.width.getValueFloat());
        GL11.glBegin(2);
        for (int i = 0; i < 360; i++) {
          Tuple<Double, Double> anglePosition = getCirclePosition3D(pos.xCoord, pos.zCoord, i, Circle.access$000(circle));
          mc.getRenderManager();
          double x = ((Double)anglePosition.getFirst()).doubleValue() - RenderManager.renderPosX;
          mc.getRenderManager();
          double z = ((Double)anglePosition.getSecond()).doubleValue() - RenderManager.renderPosZ;
          GL11.glVertex3d(x, y, z);
        } 
        GL11.glEnd();
        GL11.glEnable(3008);
        GL11.glEnable(3553);
        if (this.depth.getValue().booleanValue())
          GL11.glEnable(2929); 
      } else {
        this.circles.remove(circle);
      } 
      GL11.glPopMatrix();
    } 
  }
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public Tuple<Double, Double> getCirclePosition3D(double x, double z, double angle, double radius) {
    if (angle < 0.0D) {
      int distance = (int)Math.max(Math.abs(angle) / 360.0D, 1.0D);
      angle += (360 * distance);
    } 
    angle %= 360.0D;
    for (int i = 0; i < 360; i++) {
      if (i == (int)angle) {
        double math = i * Math.PI / 180.0D;
        return new Tuple(Double.valueOf(x - Math.sin(math) * radius), Double.valueOf(z + Math.cos(math) * radius));
      } 
    } 
    return new Tuple(Double.valueOf(x), Double.valueOf(z));
  }
  
  static class JumpCircle {}
}
