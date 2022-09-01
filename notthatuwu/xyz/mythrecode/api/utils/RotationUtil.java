package notthatuwu.xyz.mythrecode.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil {
  private static Minecraft mc = Minecraft.getMinecraft();
  
  public static float getDistanceToEntity(EntityLivingBase entityLivingBase) {
    return mc.thePlayer.getDistanceToEntity((Entity)entityLivingBase);
  }
  
  public static float getAngleChange(EntityLivingBase entityIn) {
    float yaw = getNeededRotations(entityIn)[0];
    float pitch = getNeededRotations(entityIn)[1];
    float playerYaw = mc.thePlayer.rotationYaw;
    float playerPitch = mc.thePlayer.rotationPitch;
    if (playerYaw < 0.0F)
      playerYaw += 360.0F; 
    if (playerPitch < 0.0F)
      playerPitch += 360.0F; 
    if (yaw < 0.0F)
      yaw += 360.0F; 
    if (pitch < 0.0F)
      pitch += 360.0F; 
    float yawChange = Math.max(playerYaw, yaw) - Math.min(playerYaw, yaw);
    float pitchChange = Math.max(playerPitch, pitch) - Math.min(playerPitch, pitch);
    return yawChange + pitchChange;
  }
  
  public static float[] getNeededRotations(EntityLivingBase entityIn) {
    double d0 = entityIn.posX - mc.thePlayer.posX;
    double d1 = entityIn.posZ - mc.thePlayer.posZ;
    double d2 = entityIn.posY + entityIn.getEyeHeight() - (mc.thePlayer.getEntityBoundingBox()).minY + mc.thePlayer.getEyeHeight();
    double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
    float f = (float)(MathHelper.func_181159_b(d1, d0) * 180.0D / Math.PI) - 90.0F;
    float f1 = (float)-(MathHelper.func_181159_b(d2, d3) * 180.0D / Math.PI);
    return new float[] { f, f1 };
  }
  
  public static float[] getRotation(EntityLivingBase entity) {
    return getRotation(getLocation(entity.getEntityBoundingBox()));
  }
  
  public static float[] getRotationTest(EntityLivingBase entity) {
    return getRotation(getLocation(entity.getEntityBoundingBox()));
  }
  
  public static float getYawToEntity(Entity entity) {
    EntityPlayerSP player = mc.thePlayer;
    return getYawBetween(player.rotationYaw, player.posX, player.posZ, entity.posX, entity.posZ);
  }
  
  public static float getYawBetween(float yaw, double srcX, double srcZ, double destX, double destZ) {
    double xDist = destX - srcX;
    double zDist = destZ - srcZ;
    float var1 = (float)(StrictMath.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;
    return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
  }
  
  public static float[] getAngles(EntityLivingBase entity) {
    if (entity == null)
      return null; 
    EntityPlayerSP player = mc.thePlayer;
    double diffX = entity.posX - player.posX;
    double diffY = entity.posY + entity.getEyeHeight() * 0.9D - player.posY + player.getEyeHeight();
    double diffZ = entity.posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
    float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
    return new float[] { player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw), player.rotationPitch + 
        MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch) };
  }
  
  public static float[] getRotation(Vec3 vec) {
    Vec3 playerVector = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    double y = vec.yCoord - playerVector.yCoord;
    double x = vec.xCoord - playerVector.xCoord;
    double z = vec.zCoord - playerVector.zCoord;
    double dff = Math.sqrt(x * x + z * z);
    float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(y, dff));
    return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) };
  }
  
  public static float[] faceBlock(BlockPos target) {
    EntityPlayerSP player = mc.thePlayer;
    double diffX = target.getX() + 0.5D - player.posX;
    double diffY = (target.getY() - 1) - player.posY + player.getEyeHeight();
    double diffZ = target.getZ() + 0.5D - player.posZ;
    double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(diffY, dist));
    return new float[] { yaw, pitch };
  }
  
  public static Vec3 getLocation(AxisAlignedBB bb) {
    double yaw = 0.55D;
    double pitch = 0.55D;
    Rotation.VecRotation rotation = searchCenter(bb, true);
    return (rotation != null) ? rotation.getVec() : new Vec3(bb.minX + (bb.maxX - bb.minX) * yaw, bb.minY + (bb.maxY - bb.minY) * pitch, bb.minZ + (bb.maxZ - bb.minZ) * yaw);
  }
  
  public static Rotation toRotation(Vec3 vec, boolean predict) {
    Vec3 eyesPos = new Vec3(mc.thePlayer.posX, (mc.thePlayer.getEntityBoundingBox()).minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    if (predict)
      eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ); 
    double diffX = vec.xCoord - eyesPos.xCoord;
    double diffY = vec.yCoord - eyesPos.yCoord;
    double diffZ = vec.zCoord - eyesPos.zCoord;
    return new Rotation(MathHelper.wrapAngleTo180_float(
          (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), 
        MathHelper.wrapAngleTo180_float(
          (float)-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)))));
  }
  
  private static float getAngleDifference(float a, float b) {
    return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
  }
  
  public static double getRotationDifference(Rotation a, Rotation b) {
    return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), (a.getPitch() - b.getPitch()));
  }
  
  public static double getRotationDifference180(Rotation a, Rotation b) {
    double base = getRotationDifference(a, b);
    if (base < 0.0D)
      base = -base; 
    if (base > 180.0D)
      base = 180.0D; 
    return base;
  }
  
  public static Rotation serverRotation = new Rotation(0.0F, 0.0F);
  
  public static double getRotationDifference(Rotation rotation) {
    return getRotationDifference(rotation, serverRotation);
  }
  
  public static Rotation.VecRotation searchCenter(AxisAlignedBB bb, boolean predict) {
    Rotation.VecRotation vecRotation = null;
    for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
      double ySearch;
      for (ySearch = 0.15D; ySearch < 1.0D; ySearch += 0.1D) {
        double zSearch;
        for (zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
          Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
          Rotation rotation = toRotation(vec3, predict);
          Rotation.VecRotation currentVec = new Rotation.VecRotation(vec3, rotation);
          if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
            vecRotation = currentVec; 
        } 
      } 
    } 
    return vecRotation;
  }
  
  public static boolean isVisible(Vec3 vec3) {
    Vec3 eyesPos = new Vec3(mc.thePlayer.posX, (mc.thePlayer.getEntityBoundingBox()).minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    return (mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null);
  }
  
  public static class Rotation {
    float yaw;
    
    float pitch;
    
    public Rotation(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
    }
    
    public void setYaw(float yaw) {
      this.yaw = yaw;
    }
    
    public void setPitch(float pitch) {
      this.pitch = pitch;
    }
    
    public float getYaw() {
      return this.yaw;
    }
    
    public float getPitch() {
      return this.pitch;
    }
    
    public void update(Rotation lastRotations) {
      if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch))
        return; 
      float yaw = getYaw();
      float pitch = getPitch();
      float lastYaw = lastRotations.getYaw();
      float lastPitch = lastRotations.getPitch();
      float f = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float gcd = f * f * f * 1.2F;
      float deltaYaw = yaw - lastYaw;
      float deltaPitch = pitch - lastPitch;
      float fixedDeltaYaw = deltaYaw - deltaYaw % gcd;
      float fixedDeltaPitch = deltaPitch - deltaPitch % gcd;
      float fixedYaw = lastYaw + fixedDeltaYaw;
      float fixedPitch = lastPitch + fixedDeltaPitch;
      setYaw(fixedYaw);
      setPitch(fixedPitch);
    }
    
    public static class Rotation {}
  }
  
  public static class RotationUtil {}
}
