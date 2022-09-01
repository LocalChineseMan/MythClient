package notthatuwu.xyz.mythrecode.modules.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.PlayerUtil;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import org.lwjgl.input.Keyboard;

@Info(name = "Speed", category = Category.MOVEMENT)
public class Speed extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Strafe", "NCP", "Watchdog", "Verus", "Redesky", "Bhop", "Vulcan", "Matrix", "AstralMC" }, "Strafe");
  
  public ModeSetting watchdogMode = new ModeSetting("Type", this, new String[] { "Normal", "AirSpeed", "Dev" }, "Normal", () -> Boolean.valueOf(this.mode.is("Watchdog")));
  
  public NumberSetting speed = new NumberSetting("Speed", this, 1.0D, 0.1D, 5.0D, false, () -> Boolean.valueOf(this.mode.is("Bhop")));
  
  public ModeSetting vulcanMode = new ModeSetting("Vulcan", this, new String[] { "Fast Fall", "Hop", "Dev" }, "Fast Fall", () -> Boolean.valueOf(this.mode.is("Vulcan")));
  
  public double moveSpeed;
  
  public double moveSpeed2;
  
  public boolean hasSpeedPotion;
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    int ok;
    setSuffix(this.mode.getValue());
    switch (this.mode.getValue()) {
      case "Matrix":
        if (event.isPre() && 
          mc.thePlayer.isMoving()) {
          if (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.003D) {
            mc.thePlayer.jump();
            mc.timer.timerSpeed = 1.0F;
          } 
          if (mc.thePlayer.motionY > 0.003D) {
            mc.thePlayer.motionX *= this.moveSpeed;
            mc.thePlayer.motionZ *= this.moveSpeed;
            mc.timer.timerSpeed = 1.05F;
          } 
          this.moveSpeed = 1.0011999607086182D;
        } 
        break;
      case "Redesky":
        if (mc.thePlayer.onGround) {
          mc.gameSettings.keyBindJump.pressed = true;
          return;
        } 
        if (mc.thePlayer.ticksExisted % 4 == 0)
          mc.timer.timerSpeed = 1.15F; 
        mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
        mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
        mc.gameSettings.keyBindJump.pressed = false;
        if (MoveUtil.isOnGround(0.25D) && mc.thePlayer.fallDistance > 0.0F)
          mc.timer.timerSpeed = 1.1F; 
        if (!MoveUtil.isOnGround(0.4D))
          return; 
        mc.timer.timerSpeed = 1.0F;
        break;
      case "Bhop":
        if (mc.thePlayer.onGround && event.isPre())
          mc.thePlayer.jump(); 
        mc.thePlayer.setSprinting(true);
        MoveUtil.setSpeed(this.speed.getValue().doubleValue() / 3.0D);
        break;
      case "Verus":
        if (!mc.thePlayer.isEating() && mc.thePlayer.isMoving() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally)
          mc.thePlayer.setSprinting(true); 
        if (mc.thePlayer.onGround) {
          mc.thePlayer.speedOnGround = 0.17F;
          mc.thePlayer.jump();
          MoveUtil.setSpeed(0.4099999964237213D);
        } 
        if (mc.thePlayer.isMoving() && !mc.thePlayer.onGround) {
          if (mc.thePlayer.moveForward > 0.0F) {
            MoveUtil.setSpeed((mc.thePlayer.hurtTime != 0) ? 0.5299999713897705D : 0.3499999940395355D);
          } else {
            MoveUtil.setSpeed(0.3619999885559082D);
          } 
          mc.thePlayer.speedInAir = 0.041F;
        } 
        break;
      case "Vulcan":
        switch (this.vulcanMode.getValue()) {
          case "Fast Fall":
            if (event.isPre()) {
              if (mc.thePlayer.fallDistance > 0.0F)
                mc.thePlayer.motionY = -2.0D; 
              if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                MoveUtil.setSpeed(0.45D);
              } 
            } 
            break;
          case "Dev":
            ok = 0;
            if (event.isPre() && mc.thePlayer.onGround) {
              mc.thePlayer.jump();
              MoveUtil.setSpeed(0.45D);
              mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
              mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
              if (MoveUtil.isOnGround(0.25D) && mc.thePlayer.fallDistance > 0.1F)
                mc.timer.timerSpeed = 1.1F; 
              if (mc.thePlayer.ticksExisted % 10 == 0) {
                ok++;
                if (ok < 4) {
                  mc.timer.timerSpeed = 1.11F;
                } else if (ok > 10) {
                  MoveUtil.setSpeed(0.5D);
                  ok = 0;
                } else {
                  mc.timer.timerSpeed = 1.1F;
                } 
              } 
            } 
            if (event.isPre() && mc.thePlayer.fallDistance > 0.0F)
              mc.thePlayer.motionY = -2.0D; 
            break;
          case "Hop":
            MoveUtil.strafe();
            if (event.isPre() && mc.thePlayer.onGround) {
              mc.thePlayer.jump();
              mc.thePlayer.motionX *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
              mc.thePlayer.motionZ *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0199999809265137D : 1.003999948501587D;
            } 
            break;
        } 
        break;
      case "Watchdog":
        switch (this.watchdogMode.getValue()) {
          case "Normal":
            if (mc.thePlayer.onGround) {
              mc.gameSettings.keyBindJump.pressed = true;
            } else {
              mc.gameSettings.keyBindJump.pressed = false;
            } 
            if (mc.thePlayer.isMoving()) {
              if (mc.thePlayer.fallDistance > 0.6D) {
                mc.timer.timerSpeed = 1.11F;
                mc.thePlayer.speedInAir = 0.0203F;
                break;
              } 
              if (mc.thePlayer.fallDistance > 0.2D) {
                mc.timer.timerSpeed = 1.15F;
                break;
              } 
              if (mc.thePlayer.fallDistance > 0.1D)
                mc.timer.timerSpeed = 1.16F; 
            } 
            break;
          case "AirSpeed":
            if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
              mc.thePlayer.speedInAir = 0.0204F;
              mc.timer.timerSpeed = 0.35F;
              mc.thePlayer.motionY = 0.32653D;
            } else {
              mc.timer.timerSpeed = 1.0F;
            } 
            if (mc.thePlayer.isMoving()) {
              if (mc.thePlayer.fallDistance < 0.1D)
                mc.timer.timerSpeed = 1.59F; 
              if (mc.thePlayer.fallDistance > 0.2D)
                mc.timer.timerSpeed = 0.47F; 
              if (mc.thePlayer.fallDistance > 0.6D) {
                mc.timer.timerSpeed = 1.15F;
                mc.thePlayer.speedInAir = 0.02019F;
              } 
            } 
            if (mc.thePlayer.fallDistance > 1.0F) {
              mc.timer.timerSpeed = 1.0F;
              mc.thePlayer.speedInAir = 0.02F;
            } 
            break;
          case "Dev":
            if ((mc.thePlayer.isMoving() & mc.thePlayer.onGround) != 0 && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && this.hasSpeedPotion) {
              double auxyLoveSpycat = 0.375D;
              double moveYaw = (MoveUtil.getMoveYaw(mc.thePlayer.rotationYaw) % 360.0F);
              double yaw = Math.toRadians(moveYaw);
              double x = mc.thePlayer.posX + -Math.sin(yaw) * auxyLoveSpycat;
              double z = mc.thePlayer.posZ + Math.cos(yaw) * auxyLoveSpycat;
              mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
              PacketUtil.sendPacket((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(x, mc.thePlayer.posY, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            } 
            break;
        } 
        break;
      case "Strafe":
        if (event.isPre()) {
          if (mc.thePlayer.onGround && MoveUtil.isMoving())
            mc.thePlayer.jump(); 
          MoveUtil.strafe();
        } 
        break;
      case "NCP":
        if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
          mc.timer.timerSpeed = 1.0F;
          if (event.isPost())
            mc.thePlayer.jump(); 
          MoveUtil.setFriction((float)MoveUtil.getBaseMoveSpeed(), 160.0F);
        } 
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance == 0.0F) {
          mc.thePlayer.speedInAir = 0.25F;
          MoveUtil.setSpeed(this.moveSpeed *= 0.99D);
          mc.thePlayer.speedInAir = 0.067F;
        } 
        if (mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround) {
          mc.timer.timerSpeed = 1.1F;
          MoveUtil.setSpeed(this.moveSpeed2 *= 0.95D);
        } 
        if (mc.thePlayer.onGround) {
          this.moveSpeed = MoveUtil.getBaseMoveSpeed();
          this.moveSpeed2 = MoveUtil.getBaseMoveSpeed();
        } 
        break;
    } 
  }
  
  @EventTarget
  public void onMove(EventMove event) {
    double sped, baipai, baseMoveSpeed = MoveUtil.getBaseMoveSpeed();
    switch (this.mode.getValue()) {
      case "AstralMC":
        sped = baseMoveSpeed + 0.86D;
        if (MoveUtil.isMoving() && mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 3 == 0) {
          MoveUtil.setSpeed(sped);
          break;
        } 
        baipai = 1.01D;
        event.setX(event.getX() * baipai);
        event.setZ(event.getZ() * baipai);
        break;
    } 
  }
  
  public void onEnable() {
    this.moveSpeed = MoveUtil.getBaseMoveSpeed();
    this.moveSpeed2 = MoveUtil.getBaseMoveSpeed();
    if (this.mode.is("Watchdog") && this.watchdogMode.is("Dev")) {
      if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
        this.hasSpeedPotion = true; 
      if (this.hasSpeedPotion) {
        if (mc.thePlayer.onGround) {
          PlayerUtil.stopWalk();
          mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.85D, mc.thePlayer.posZ);
          mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
          PlayerUtil.resumeWalk();
        } 
      } else {
        Client.INSTANCE.notificationManager.sendNotification("You need a Speed II pot, to you use the speed!", "Speed");
        toggle();
      } 
    } 
  }
  
  public void onDisable() {
    this.hasSpeedPotion = false;
    mc.thePlayer.speedInAir = 0.02F;
    mc.timer.timerSpeed = 1.0F;
    super.onDisable();
    mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
  }
}
