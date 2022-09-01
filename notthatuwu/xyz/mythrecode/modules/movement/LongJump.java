package notthatuwu.xyz.mythrecode.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.PlayerUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.EventTick;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "LongJump", category = Category.MOVEMENT)
public class LongJump extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Bow", "Redesky", "Vanilla", "Watchdog Bow" }, "Bow");
  
  public ModeSetting redeskymode = new ModeSetting("Type", this, new String[] { "High", "Long", "Dev" }, "Long", () -> Boolean.valueOf(this.mode.is("Redesky")));
  
  public NumberSetting bowmotion = new NumberSetting("Motion", this, 0.4D, 0.1D, 2.0D, false, () -> Boolean.valueOf((this.mode.is("Bow") || this.mode.is("Vanilla"))));
  
  public NumberSetting bowspeed = new NumberSetting("Speed", this, 0.1D, 0.1D, 2.5D, false, () -> Boolean.valueOf((this.mode.is("Bow") || this.mode.is("Vanilla"))));
  
  public BooleanSetting sameY = new BooleanSetting("Same Y", this, false);
  
  private int ticks;
  
  private int ticks1;
  
  private int ticks2;
  
  private int ticks3;
  
  private int i;
  
  private int slotId;
  
  private int tick;
  
  private int startY;
  
  private boolean damaged;
  
  private double moveSpeed;
  
  private TimeHelper timeHelper = new TimeHelper();
  
  private TimeHelper startTimeHelper = new TimeHelper();
  
  @EventTarget
  public void onMotion(EventUpdate event) {
    if (this.sameY.getValue().booleanValue()) {
      Entity ent = mc.getRenderViewEntity();
      double y = mc.thePlayer.posY - this.startY;
      (mc.getRenderViewEntity()).posY -= y;
    } 
    if (this.mode.is("Redesky") ? (this.tick > 10) : (this.mode.is("Vulcan") ? (this.tick > 14) : (this.tick > 30 && !this.mode.is("Watchdog bow"))))
      if (mc.thePlayer.onGround)
        toggle();  
    if (this.mode.is("Redesky"))
      switch (this.redeskymode.getValue()) {
        case "Long":
          if (mc.thePlayer.onGround && !this.damaged) {
            mc.thePlayer.jump();
            if (mc.thePlayer.motionY > 0.0D && mc.thePlayer.onGround) {
              mc.thePlayer.motionY += 0.054D;
              mc.thePlayer.speedInAir = 0.04F;
              this.damaged = true;
            } 
          } 
          break;
        case "High":
          mc.thePlayer.speedInAir = 0.02F;
          mc.timer.timerSpeed = 1.0F;
          if (mc.thePlayer.onGround && !this.damaged && 
            mc.thePlayer.isMoving()) {
            if (!mc.gameSettings.keyBindJump.isKeyDown())
              mc.thePlayer.jump(); 
            mc.thePlayer.motionY += 0.3D;
            mc.thePlayer.motionX *= 1.2D;
            mc.thePlayer.motionZ *= 1.2D;
            this.damaged = true;
          } 
          break;
        case "Dev":
          if (this.damaged && mc.thePlayer.motionY > 0.0D)
            mc.thePlayer.motionY += 0.02D; 
          if (mc.thePlayer.onGround && this.ticks < 5.0F && !this.damaged) {
            mc.timer.timerSpeed = 0.95F;
            mc.thePlayer.motionY = 0.9200000166893005D;
            MoveUtil.strafe(MoveUtil.getSpeed() * 1.649999976158142D);
            this.damaged = true;
          } 
          this.ticks = (int)(this.ticks + 1.0F);
          break;
      }  
    switch (this.mode.getValue()) {
      case "Watchdog Bow":
        if (!this.damaged)
          event.setPitch(-90.0F); 
        if (mc.thePlayer.hurtTime == 0 && !this.damaged) {
          this.ticks3++;
          this.damaged = false;
          if (this.ticks3 > 2)
            MoveUtil.setSpeed(0.0D); 
        } else {
          this.damaged = true;
        } 
        if (this.damaged) {
          MoveUtil.resumeWalk();
          this.ticks2++;
          if (this.ticks2 == 1) {
            MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 0.4D);
            break;
          } 
          if (this.ticks2 == 2) {
            MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 0.3D);
            break;
          } 
          if (this.ticks2 < 11) {
            MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 0.2D);
            break;
          } 
          if (this.ticks2 < 18) {
            MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 0.1D);
            break;
          } 
          MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed());
        } 
        break;
      case "Vanilla":
        if (!this.damaged) {
          if (mc.thePlayer.onGround)
            mc.thePlayer.motionY = this.bowmotion.getValue().doubleValue(); 
          MoveUtil.setSpeed(this.bowspeed.getValue().doubleValue());
          this.damaged = true;
        } 
        break;
      case "Bow":
        if (mc.thePlayer.hurtTime == 9)
          this.damaged = true; 
        if (!this.damaged) {
          PlayerUtil.stopWalk();
          if (mc.thePlayer.ticksExisted - this.ticks == 3) {
            mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -89.5F, true));
            mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
            if (this.i != this.slotId)
              mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C09PacketHeldItemChange(this.slotId)); 
          } 
        } 
        if (this.damaged) {
          PlayerUtil.resumeWalk();
          if (mc.thePlayer.onGround)
            mc.thePlayer.motionY = this.bowmotion.getValue().doubleValue(); 
          MoveUtil.setSpeed(this.bowspeed.getValue().doubleValue());
        } 
        break;
    } 
  }
  
  @EventTarget
  public void onTick(EventTick eventTick) {
    if (this.mode.is("Watchdog Bow")) {
      this.ticks++;
      if (this.ticks == 4) {
        MoveUtil.stopWalk();
        PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      } 
      if (this.ticks == 8)
        mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN)); 
      if (this.damaged) {
        MoveUtil.resumeWalk();
        this.ticks1++;
        if (this.ticks1 == 1) {
          mc.thePlayer.motionY = 0.8D;
        } else if (this.ticks1 < 3) {
        
        } 
        if (this.ticks1 > 20 && 
          mc.thePlayer.onGround)
          toggle(); 
      } 
    } 
    this.tick++;
  }
  
  public void onEnable() {
    ItemStack itemStack;
    this.timeHelper.reset();
    this.startTimeHelper.reset();
    this.ticks = 0;
    this.ticks1 = 0;
    this.ticks2 = 0;
    this.ticks3 = 0;
    this.i = 0;
    this.damaged = false;
    this.tick = 0;
    this.startY = (int)mc.thePlayer.posY;
    switch (this.mode.getValue()) {
      case "Watchdog Bow":
        if (!mc.thePlayer.inventory.hasItem(Items.arrow)) {
          toggle();
          return;
        } 
        itemStack = null;
        for (this.i = 0; this.i < 9; this.i++) {
          itemStack = mc.thePlayer.inventory.mainInventory[this.i];
          if (itemStack != null && itemStack.getItem() instanceof net.minecraft.item.ItemBow)
            break; 
        } 
        this.slotId = mc.thePlayer.inventory.currentItem;
        if (this.i != this.slotId)
          mc.thePlayer.inventory.currentItem = this.i; 
        break;
      case "Bow":
        this.damaged = false;
        if (!mc.thePlayer.inventory.hasItem(Items.arrow)) {
          toggle();
          return;
        } 
        itemStack = null;
        for (this.i = 0; this.i < 9; this.i++) {
          itemStack = mc.thePlayer.inventory.mainInventory[this.i];
          if (itemStack != null && itemStack.getItem() instanceof net.minecraft.item.ItemBow)
            break; 
        } 
        if (this.i == 9) {
          ChatUtil.sendChatMessageWPrefix("You need a bow");
          toggle();
          return;
        } 
        this.slotId = mc.thePlayer.inventory.currentItem;
        if (this.i != this.slotId)
          mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C09PacketHeldItemChange(this.i)); 
        this.ticks = mc.thePlayer.ticksExisted;
        mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C08PacketPlayerBlockPlacement(itemStack));
        if (this.damaged) {
          if (!mc.thePlayer.onGround)
            return; 
          for (int i = 0; i < 10; i++) {
            mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            double fallDistance = 3.0125D;
            while (fallDistance > 0.0D) {
              mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698D, mc.thePlayer.posZ, false));
              mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212D, mc.thePlayer.posZ, false));
              mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.3579E-6D, mc.thePlayer.posZ, false));
              fallDistance -= 0.7531999805212D;
            } 
            mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            mc.thePlayer.jump();
            mc.thePlayer.posY += 0.42D;
          } 
        } 
        break;
    } 
  }
  
  public void damage() {
    mc.thePlayer.motionY = 0.7D;
    PacketUtil.sendPacketNoEvent((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.21D, mc.thePlayer.posZ, false));
  }
  
  public void onDisable() {
    super.onDisable();
    mc.timer.timerSpeed = 1.0F;
    mc.thePlayer.capabilities.isCreativeMode = false;
    mc.thePlayer.capabilities.allowFlying = false;
    mc.thePlayer.capabilities.isFlying = false;
    mc.thePlayer.speedInAir = 0.02F;
    mc.thePlayer.stepHeight = 0.5F;
    mc.thePlayer.motionX = 0.0D;
    mc.thePlayer.motionY = 0.0D;
    mc.thePlayer.motionZ = 0.0D;
  }
}
