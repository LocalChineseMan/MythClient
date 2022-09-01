package notthatuwu.xyz.mythrecode.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import javax.vecmath.Vector2d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ColorUtil;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.events.Event3D;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.movement.Fly;
import notthatuwu.xyz.mythrecode.modules.movement.Speed;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

@Info(name = "TargetStrafe", category = Category.COMBAT)
public class TargetStrafe extends Module {
  private Entity entityLivingBase;
  
  private boolean shouldStrafe;
  
  private boolean canStrafe;
  
  private double moveDirection = 1.0D;
  
  private static boolean strafing;
  
  private Point currentPoint;
  
  public static boolean isStrafing() {
    return strafing;
  }
  
  private int currentIndex = 0;
  
  private ArrayList<Point> points = new ArrayList<>();
  
  public ModeSetting renderMode = new ModeSetting("Render Mode", new String[] { "None", "Cricle", "Points" }, "None");
  
  public NumberSetting lineWidth = new NumberSetting("Line Width", this, 1.8D, 0.1D, 3.0D, false);
  
  public NumberSetting radius = new NumberSetting("Radius", this, 1.6D, 0.0D, 3.0D, false);
  
  public NumberSetting pointAmount = new NumberSetting("Points", this, 11.0D, 2.0D, 48.0D, true);
  
  public NumberSetting slowdown = new NumberSetting("Slowdown", this, 1.0D, 0.1D, 1.0D, false);
  
  public BooleanSetting pressSpaceOnly = new BooleanSetting("Press Space Only", this, false);
  
  public BooleanSetting checkVoid = new BooleanSetting("Check Void", this, false);
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (event.isPre()) {
      if (mc.thePlayer == null || mc.theWorld == null)
        return; 
      boolean newTarget = (KillAura.target != this.entityLivingBase);
      if (newTarget)
        this.entityLivingBase = (Entity)KillAura.target; 
      if (this.entityLivingBase != null) {
        updatePoints();
        if (this.currentPoint == null || newTarget) {
          this.currentPoint = getBestPoint();
          this.currentIndex = this.points.indexOf(this.currentPoint);
        } else {
          boolean switchedDir = false;
          if (mc.thePlayer.isCollidedHorizontally || (this.checkVoid.getValue().booleanValue() && !MoveUtil.isBlockUnder())) {
            if (this.moveDirection == -1.0D) {
              this.moveDirection = 1.0D;
            } else {
              this.moveDirection = -1.0D;
            } 
            switchedDir = true;
          } 
          if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            this.moveDirection = 1.0D;
            switchedDir = true;
          } 
          if (mc.gameSettings.keyBindRight.isKeyDown()) {
            this.moveDirection = -1.0D;
            switchedDir = true;
          } 
          if (getDistanceTo(this.currentPoint) < MoveUtil.getSpeedDistance() || switchedDir) {
            this.currentIndex = (int)(this.currentIndex + this.moveDirection);
            if (this.currentIndex > this.points.size() - 1)
              this.currentIndex = 0; 
            if (this.currentIndex < 0)
              this.currentIndex = this.points.size() - 1; 
            int i = 0;
            while (!((Point)this.points.get(this.currentIndex)).valid && i < this.points.size()) {
              this.currentIndex = (int)(this.currentIndex + this.moveDirection);
              if (this.currentIndex < 0)
                this.currentIndex = this.points.size() - 1; 
              if (this.currentIndex > this.points.size() - 1)
                this.currentIndex = 0; 
              i++;
            } 
          } 
        } 
        if (this.currentIndex < 0)
          this.currentIndex = this.points.size() - 1; 
        if (this.currentIndex > this.points.size() - 1)
          this.currentIndex = 0; 
        this.currentPoint = this.points.get(this.currentIndex);
      } 
    } 
  }
  
  @EventTarget
  public void render3D(Event3D evnet) {
    KillAura killAuraModule = (KillAura)Client.INSTANCE.moduleManager.getModuleByClass(KillAura.class);
    this.canStrafe = (GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || !this.pressSpaceOnly.getValue().booleanValue());
    this.shouldStrafe = (this.entityLivingBase != null && killAuraModule.isEnabled());
    if (this.shouldStrafe) {
      if (this.renderMode.is("circle"))
        drawCircle(this.entityLivingBase, this.lineWidth
            
            .getValue().floatValue(), this.radius
            .getValue().floatValue() - 0.3D); 
      if (this.renderMode.is("points")) {
        double[] position = RenderUtils.getInterpolatedPosition(this.entityLivingBase);
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(2832);
        GL11.glDepthMask(false);
        GL11.glPointSize(10.0F);
        GL11.glBegin(0);
        int i = this.points.size();
        for (Point point : this.points) {
          ColorUtil.doColor((point == this.currentPoint) ? (new Color(43, 214, 60)).getRGB() : (new Color(87, 87, 87)).getRGB());
          mc.getRenderManager();
          mc.getRenderManager();
          GL11.glVertex3d(point.position.x - RenderManager.viewerPosX, position[1], point.position.y - RenderManager.viewerPosZ);
          i--;
        } 
        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glDisable(2832);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
      } 
    } 
  }
  
  public EventMove editMovement(double x, double y, double z) {
    double movementSpeed = Math.sqrt(x * x + z * z) * this.slowdown.getValue().doubleValue();
    boolean modulesEnabled = (((Fly)Client.INSTANCE.moduleManager.getModuleByClass(Fly.class)).isEnabled() || ((Speed)Client.INSTANCE.moduleManager.getModuleByClass(Speed.class)).isEnabled());
    if (this.canStrafe && mc.thePlayer.isMoving() && modulesEnabled && this.shouldStrafe && 
      !MoveUtil.isInLiquid() && !mc.thePlayer.isOnLadder()) {
      strafing = true;
      float yaw = getYawTo(this.currentPoint);
      yaw = MoveUtil.getDirection(1.0F, 0.0F, yaw);
      x = -Math.sin(Math.toRadians(yaw)) * movementSpeed;
      z = Math.cos(Math.toRadians(yaw)) * movementSpeed;
    } 
    return new EventMove(x, y, z);
  }
  
  private void updatePoints() {
    this.points.clear();
    int size = this.pointAmount.getValue().intValue();
    for (int i = 0; i < size; i++) {
      double cos = this.radius.getValue().doubleValue() * Math.cos(i * 6.283185307179586D / size);
      double sin = this.radius.getValue().doubleValue() * Math.sin(i * 6.283185307179586D / size);
      double pointX = this.entityLivingBase.posX + cos;
      double pointZ = this.entityLivingBase.posZ + sin;
      Point point = new Point(new Vector2d(pointX, pointZ));
      this.points.add(point);
    } 
  }
  
  private Point getBestPoint() {
    double closest = Double.MAX_VALUE;
    Point bestPoint = null;
    for (Point point : this.points) {
      if (point.valid) {
        double dist = getDistanceTo(point);
        if (dist < closest) {
          closest = dist;
          bestPoint = point;
        } 
      } 
    } 
    return bestPoint;
  }
  
  private double getDistanceTo(Point point) {
    double xDist = point.position.x - mc.thePlayer.posX;
    double zDist = point.position.y - mc.thePlayer.posZ;
    return Math.sqrt(xDist * xDist + zDist * zDist);
  }
  
  private float getYawTo(Point point) {
    if (point == null)
      return mc.thePlayer.rotationYaw; 
    double xDist = point.position.x - mc.thePlayer.posX;
    double zDist = point.position.y - mc.thePlayer.posZ;
    float rotationYaw = mc.thePlayer.rotationYaw;
    float var1 = (float)(StrictMath.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;
    return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
  }
  
  private void drawCircle(Entity entity, float lineWidth, double radius) {
    GlStateManager.pushAttrib();
    GlStateManager.pushMatrix();
    GL11.glPushMatrix();
    mc.entityRenderer.disableLightmap();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(2929);
    GL11.glEnable(2848);
    GL11.glDepthMask(false);
    mc
      .getRenderManager();
    double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.viewerPosX;
    mc
      .getRenderManager();
    double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.viewerPosY;
    mc
      .getRenderManager();
    double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.viewerPosZ;
    GL11.glPushMatrix();
    GL11.glLineWidth(lineWidth);
    Cylinder c = new Cylinder();
    GL11.glTranslated(posX, posY, posZ);
    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
    c.setDrawStyle(100011);
    GlStateManager.resetColor();
    RenderUtils.color(Color.WHITE.getRGB());
    c.draw((float)(radius + 0.25D), (float)(radius + 0.25D), 0.0F, this.pointAmount.getValue().intValue(), 0);
    c.draw((float)(radius + 0.25D), (float)(radius + 0.25D), 0.0F, this.pointAmount.getValue().intValue(), 0);
    GL11.glPopMatrix();
    GL11.glDepthMask(true);
    GL11.glDisable(2848);
    GL11.glEnable(2929);
    GL11.glDisable(3042);
    GL11.glEnable(3553);
    mc.entityRenderer.enableLightmap();
    GL11.glPopMatrix();
    GlStateManager.popMatrix();
    GlStateManager.popAttrib();
  }
  
  public static class Point {
    Vector2d position;
    
    boolean valid;
    
    public Point(Vector2d position) {
      this.position = position;
      this.valid = isPointValid(position);
    }
    
    private boolean isPointValid(Vector2d position) {
      Vec3 pointVec = new Vec3(position.x, Module.mc.thePlayer.posY, position.y);
      IBlockState blockState = Module.mc.theWorld.getBlockState(new BlockPos(pointVec));
      boolean canBeSeen = (Module.mc.theWorld.rayTraceBlocks(Module.mc.thePlayer.getPositionVector(), pointVec, false, true, false) == null);
      boolean isAboveVoid = isBlockUnder(position.x, position.y, 5.0D);
      return (!blockState.getBlock().isFullBlock() && canBeSeen && (!isAboveVoid || ((Fly)Client.INSTANCE.moduleManager.getModuleByClass(Fly.class)).isEnabled()));
    }
    
    private boolean isBlockUnder(double posX, double posZ, double height) {
      for (int i = (int)Module.mc.thePlayer.posY; i >= Module.mc.thePlayer.posY - height; i--) {
        if (!(Module.mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof net.minecraft.block.BlockAir))
          return false; 
      } 
      return true;
    }
  }
}
