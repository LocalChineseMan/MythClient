package notthatuwu.xyz.mythrecode.api.utils;

import javax.vecmath.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import notthatuwu.xyz.mythrecode.events.EventMove;
import org.lwjgl.input.Keyboard;

public class MoveUtil {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static void setSpeed(EventMove e, double speed) {
    EntityPlayerSP player = mc.thePlayer;
    setSpeed(e, speed, player.rotationYaw, player.moveForward, player.moveStrafing);
  }
  
  public static void resumeWalk() {
    mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
    mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
    mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
    mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
  }
  
  public static void stopWalk() {
    mc.gameSettings.keyBindForward.pressed = false;
    mc.gameSettings.keyBindBack.pressed = false;
    mc.gameSettings.keyBindLeft.pressed = false;
    mc.gameSettings.keyBindRight.pressed = false;
  }
  
  public static double getSpeedDistance() {
    double distX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
    double distZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
    return Math.sqrt(distX * distX + distZ * distZ);
  }
  
  public static float setFriction(float speed, float friction) {
    float percent = friction;
    float value = speed / 100.0F * percent;
    return value;
  }
  
  public static double[] yawPos(double value) {
    return yawPos(mc.thePlayer.rotationYaw * 0.017453292F, value);
  }
  
  public static double[] yawPos(float yaw, double value) {
    return new double[] { -MathHelper.sin(yaw) * value, MathHelper.cos(yaw) * value };
  }
  
  public static void setSpeed(EventMove moveEvent, double moveSpeed, float yaw, double forward, double strafe) {
    if (forward != 0.0D) {
      if (strafe > 0.0D) {
        yaw += ((forward > 0.0D) ? -45 : 45);
      } else if (strafe < 0.0D) {
        yaw += ((forward > 0.0D) ? 45 : -45);
      } 
      strafe = 0.0D;
      if (forward > 0.0D) {
        forward = 1.0D;
      } else if (forward < 0.0D) {
        forward = -1.0D;
      } 
    } 
    if (strafe > 0.0D) {
      strafe = 1.0D;
    } else if (strafe < 0.0D) {
      strafe = -1.0D;
    } 
    double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
    double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
    moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
    moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
  }
  
  public static void setSpeed(double moveSpeed) {
    float rotationYaw = mc.thePlayer.rotationYaw;
    MovementInput movementInput = mc.thePlayer.movementInput;
    double strafe = MovementInput.moveStrafe;
    MovementInput movementInput2 = mc.thePlayer.movementInput;
    setSpeed(moveSpeed, rotationYaw, strafe, MovementInput.moveForward);
  }
  
  public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
    if (forward != 0.0D) {
      if (strafe > 0.0D) {
        yaw += ((forward > 0.0D) ? -45 : 45);
      } else if (strafe < 0.0D) {
        yaw += ((forward > 0.0D) ? 45 : -45);
      } 
      strafe = 0.0D;
      if (forward > 0.0D) {
        forward = 1.0D;
      } else if (forward < 0.0D) {
        forward = -1.0D;
      } 
    } 
    if (strafe > 0.0D) {
      strafe = 1.0D;
    } else if (strafe < 0.0D) {
      strafe = -1.0D;
    } 
    double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
    double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
    mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
    mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
  }
  
  public static float getMoveYaw(float yaw) {
    Vector2f from = new Vector2f((float)mc.thePlayer.lastTickPosX, (float)mc.thePlayer.lastTickPosZ);
    Vector2f to = new Vector2f((float)mc.thePlayer.posX, (float)mc.thePlayer.posZ);
    Vector2f diff = new Vector2f(to.x - from.x, to.y - from.y);
    double x = diff.x, z = diff.y;
    if (x != 0.0D || z != 0.0D)
      yaw = (float)Math.toDegrees((Math.atan2(-x, z) + 6.2831854820251465D) % 6.2831854820251465D); 
    return yaw;
  }
  
  public static boolean isBlockUnder() {
    for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
      AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0.0D, -offset, 0.0D);
      if (!mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, boundingBox).isEmpty())
        return true; 
    } 
    return false;
  }
  
  public static void strafe(double speed) {
    if (!isMoving())
      return; 
    double yaw = getDirection();
    mc.thePlayer.motionX = -Math.sin(yaw) * speed;
    mc.thePlayer.motionZ = Math.cos(yaw) * speed;
  }
  
  public static float getDirection() {
    return (float)Math.toRadians(getDirection(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw));
  }
  
  public static float getDirection(float forward, float strafing, float yaw) {
    if (forward == 0.0D && strafing == 0.0D)
      return yaw; 
    boolean reversed = (forward < 0.0D);
    float strafingYaw = 90.0F * ((forward > 0.0F) ? 0.5F : (reversed ? -0.5F : 1.0F));
    if (reversed)
      yaw += 180.0F; 
    if (strafing > 0.0F) {
      yaw -= strafingYaw;
    } else if (strafing < 0.0F) {
      yaw += strafingYaw;
    } 
    return yaw;
  }
  
  public static void strafe() {
    strafe(getSpeed());
  }
  
  public static boolean isMoving() {
    // Byte code:
    //   0: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   3: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   6: ifnull -> 49
    //   9: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   12: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   15: getfield movementInput : Lnet/minecraft/util/MovementInput;
    //   18: pop
    //   19: getstatic net/minecraft/util/MovementInput.moveForward : F
    //   22: fconst_0
    //   23: fcmpl
    //   24: ifne -> 45
    //   27: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   30: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   33: getfield movementInput : Lnet/minecraft/util/MovementInput;
    //   36: pop
    //   37: getstatic net/minecraft/util/MovementInput.moveStrafe : F
    //   40: fconst_0
    //   41: fcmpl
    //   42: ifeq -> 49
    //   45: iconst_1
    //   46: goto -> 50
    //   49: iconst_0
    //   50: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #187	-> 0
  }
  
  public static boolean isOnGround(double height) {
    return !mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
  }
  
  public static boolean isOnGround() {
    return (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically);
  }
  
  public static double getBaseMoveSpeed() {
    double[] values = { 1.0D, 1.4304347400741908D, 1.7347825295420374D, 1.9217391028296074D };
    EntityPlayerSP player = mc.thePlayer;
    double base = player.isSneaking() ? 0.06630000288486482D : (canSprint(true) ? 0.2872999905467033D : 0.22100000083446503D);
    PotionEffect moveSpeed = player.getActivePotionEffect(Potion.moveSpeed);
    PotionEffect moveSlowness = player.getActivePotionEffect(Potion.moveSlowdown);
    if (moveSpeed != null)
      base *= 1.0D + 0.2D * (moveSpeed.getAmplifier() + 1); 
    if (moveSlowness != null)
      base *= 1.0D + 0.2D * (moveSlowness.getAmplifier() + 1); 
    if (player.isInWater()) {
      base *= 0.5203619984250619D;
      int depthStriderLevel = EnchantmentHelper.getDepthStriderModifier((Entity)mc.thePlayer);
      if (depthStriderLevel > 0)
        base *= values[depthStriderLevel]; 
    } else if (player.isInLava()) {
      base *= 0.5203619984250619D;
    } 
    return base;
  }
  
  public static boolean canSprint(boolean omni) {
    // Byte code:
    //   0: iload_0
    //   1: ifeq -> 12
    //   4: invokestatic isMovingEnoughForSprint : ()Z
    //   7: ifne -> 34
    //   10: iconst_0
    //   11: ireturn
    //   12: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   15: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   18: getfield movementInput : Lnet/minecraft/util/MovementInput;
    //   21: pop
    //   22: getstatic net/minecraft/util/MovementInput.moveForward : F
    //   25: ldc_w 0.8
    //   28: fcmpg
    //   29: ifge -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   37: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   40: getfield isCollidedHorizontally : Z
    //   43: ifne -> 143
    //   46: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   49: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   52: invokevirtual getFoodStats : ()Lnet/minecraft/util/FoodStats;
    //   55: invokevirtual getFoodLevel : ()I
    //   58: bipush #6
    //   60: if_icmpgt -> 78
    //   63: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   66: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   69: getfield capabilities : Lnet/minecraft/entity/player/PlayerCapabilities;
    //   72: getfield allowFlying : Z
    //   75: ifeq -> 143
    //   78: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   81: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   84: invokevirtual isSneaking : ()Z
    //   87: ifne -> 143
    //   90: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   93: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   96: invokevirtual isUsingItem : ()Z
    //   99: ifeq -> 121
    //   102: getstatic notthatuwu/xyz/mythrecode/Client.INSTANCE : Lnotthatuwu/xyz/mythrecode/Client;
    //   105: getfield moduleManager : Lnotthatuwu/xyz/mythrecode/api/module/ModuleManager;
    //   108: pop
    //   109: ldc_w 'NoSlow'
    //   112: invokestatic getModuleByName : (Ljava/lang/String;)Lnotthatuwu/xyz/mythrecode/api/module/Module;
    //   115: invokevirtual isEnabled : ()Z
    //   118: ifeq -> 143
    //   121: getstatic notthatuwu/xyz/mythrecode/api/utils/MoveUtil.mc : Lnet/minecraft/client/Minecraft;
    //   124: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   127: getstatic net/minecraft/potion/Potion.moveSlowdown : Lnet/minecraft/potion/Potion;
    //   130: getfield id : I
    //   133: invokevirtual isPotionActive : (I)Z
    //   136: ifne -> 143
    //   139: iconst_1
    //   140: goto -> 144
    //   143: iconst_0
    //   144: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #225	-> 0
    //   #226	-> 4
    //   #227	-> 10
    //   #230	-> 12
    //   #231	-> 32
    //   #233	-> 34
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	145	0	omni	Z
  }
  
  private static boolean isMovingEnoughForSprint() {
    MovementInput movementInput = mc.thePlayer.movementInput;
    if (MovementInput.moveForward <= 0.8F)
      if (MovementInput.moveForward >= -0.8F)
        if (MovementInput.moveStrafe <= 0.8F) {
          if (MovementInput.moveStrafe < -0.8F);
          return false;
        }   
  }
  
  public static double getBaseMovementSpeed() {
    double baseSpeed = 0.29D;
    if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
      baseSpeed *= 1.0D + 0.2D * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1); 
    return baseSpeed;
  }
  
  public static boolean isInLiquid() {
    return (mc.thePlayer.isInWater() || mc.thePlayer.isInLava());
  }
  
  public static float getSpeed() {
    return (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
  }
  
  public static boolean isOverVoid() {
    for (double posY = mc.thePlayer.posY; posY > 0.0D; posY--) {
      if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ)).getBlock() instanceof net.minecraft.block.BlockAir))
        return false; 
    } 
    return true;
  }
}
