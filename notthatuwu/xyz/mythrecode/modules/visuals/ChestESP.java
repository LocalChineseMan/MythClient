package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.events.Event3D;
import org.lwjgl.opengl.GL11;

@Info(name = "ChestESP", category = Category.VISUAL)
public class ChestESP extends Module {
  public ColorSetting color = new ColorSetting("Color", this, Color.RED);
  
  @EventTarget
  public void onRender34(Event3D event) {
    for (Object o : mc.theWorld.loadedTileEntityList) {
      if (o instanceof TileEntityChest) {
        TileEntityLockable storage = (TileEntityLockable)o;
        drawESPOnStorage(storage, storage.getPos().getX(), storage.getPos().getY(), storage.getPos().getZ());
      } 
    } 
  }
  
  public void drawESPOnStorage(TileEntityLockable storage, double x, double y, double z) {
    assert !storage.isLocked();
    TileEntityChest chest = (TileEntityChest)storage;
    Vec3 vec = new Vec3(0.0D, 0.0D, 0.0D);
    Vec3 vec2 = new Vec3(0.0D, 0.0D, 0.0D);
    if (chest.adjacentChestZNeg != null) {
      vec = new Vec3(x + 0.0625D, y, z - 0.9375D);
      vec2 = new Vec3(x + 0.9375D, y + 0.875D, z + 0.9375D);
    } else if (chest.adjacentChestXNeg != null) {
      vec = new Vec3(x + 0.9375D, y, z + 0.0625D);
      vec2 = new Vec3(x - 0.9375D, y + 0.875D, z + 0.9375D);
    } else {
      if (chest.adjacentChestXPos != null || chest.adjacentChestZPos != null)
        return; 
      vec = new Vec3(x + 0.0625D, y, z + 0.0625D);
      vec2 = new Vec3(x + 0.9375D, y + 0.875D, z + 0.9375D);
    } 
    GL11.glPushMatrix();
    RenderUtils.pre3D();
    GlStateManager.disableDepth();
    mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
    GL11.glColor4f(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 0.3F);
    RenderUtils.drawFilledBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
    GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.enableDepth();
    RenderUtils.post3D();
    GL11.glPopMatrix();
  }
}
