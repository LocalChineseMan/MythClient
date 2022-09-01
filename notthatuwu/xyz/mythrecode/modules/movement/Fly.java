package notthatuwu.xyz.mythrecode.modules.movement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.PlayerUtil;
import notthatuwu.xyz.mythrecode.api.utils.RandomUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.EventCollide;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.exploit.Disabler;

@Info(name = "Fly", category = Category.MOVEMENT)
public class Fly extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { 
        "Motion", "Minemora", "Minemora Glide", "Hycraft", "Matrix", "Bow", "Jump", "Collison", "Damage", "Teleport", 
        "Slime", "Dev", "RedeSky" }, "Motion");
  
  public ModeSetting matrixmode = new ModeSetting("Type", this, new String[] { "Glide", "Blink" }, "Glide", () -> Boolean.valueOf(this.mode.is("Matrix")));
  
  public ModeSetting hycraftMode = new ModeSetting("Hycraft", this, new String[] { "Clip", "Blink" }, "Clip", () -> Boolean.valueOf(this.mode.is("Hycraft")));
  
  public NumberSetting boost = new NumberSetting("Timer Boost", this, 0.76D, 0.1D, 2.0D, false, () -> Boolean.valueOf((this.matrixmode.is("Glide") && this.mode.is("Matrix"))));
  
  public NumberSetting timer = new NumberSetting("Timer", this, 1.0D, 0.1D, 2.0D, false, () -> Boolean.valueOf(this.mode.is("Damage")));
  
  public NumberSetting speed = new NumberSetting("Speed", this, 0.4D, 0.1D, 3.0D, false, () -> Boolean.valueOf((this.mode.getValue().equalsIgnoreCase("Motion") || this.mode.is("minemora") || this.mode.getValue().equalsIgnoreCase("Collison") || this.mode.is("Damage"))));
  
  public NumberSetting packetdelay = new NumberSetting("Teleport Delay", this, 5.0D, 1.0D, 25.0D, false, () -> Boolean.valueOf(this.mode.is("Teleport")));
  
  public NumberSetting tplenght = new NumberSetting("Teleport Lenght", this, 5.0D, 1.0D, 20.0D, false, () -> Boolean.valueOf(this.mode.is("Teleport")));
  
  public BooleanSetting bobbing = new BooleanSetting("Bobbing", this, true);
  
  private boolean damaged;
  
  private boolean slime = false;
  
  private boolean can = false;
  
  private int ticks;
  
  private int slotId;
  
  private int i = 0;
  
  private int delay;
  
  private int start = 0;
  
  private int start2 = 0;
  
  private int stage = 0;
  
  private TimeHelper timeHelper = new TimeHelper();
  
  private final List<Packet> blinkPackets = new CopyOnWriteArrayList<>();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    int test;
    setSuffix(this.mode.getValue());
    if (this.bobbing.getValue().booleanValue())
      mc.thePlayer.cameraYaw = 0.1F; 
    switch (this.mode.getValue()) {
      case "RedeSky":
        setTimer(0.7F);
        (getPlayer()).capabilities.isFlying = true;
        (getPlayer()).capabilities.setFlySpeed((float)(Math.random() / 5.0D));
        break;
      case "Slime":
        event.setPitch(90.0F);
        if (this.slime && mc.thePlayer.ticksExisted - this.start > 5) {
          ItemStack stack = null;
          for (int i = 36; i < 45; i++) {
            stack = (getPlayer()).inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
              Item item = stack.getItem();
              if (item instanceof ItemBlock) {
                ItemBlock block = (ItemBlock)item;
                if (Block.getBlockFromItem(item) instanceof net.minecraft.block.BlockSlime)
                  mc.thePlayer.inventory.currentItem = i - 36; 
              } 
            } 
          } 
          PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
          PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, Math.round(mc.thePlayer.posY - 2.0D), mc.thePlayer.posZ), 1, stack, 0.61F, 0.41F, 0.32F));
          PacketUtil.sendPacket((Packet)new C0APacketAnimation());
          ChatUtil.sendChatMessageWPrefix("Placed Block!");
          this.slime = false;
          this.start2 = mc.thePlayer.ticksExisted;
          this.can = true;
        } 
        if (this.can && mc.thePlayer.ticksExisted - this.start2 > 1)
          mc.thePlayer.motionY = 0.0D; 
        break;
      case "Dev":
        test = 0;
        mc.thePlayer.motionY = 0.0D;
        PacketUtil.sendFunnyPacket();
        break;
      case "Teleport":
        mc.thePlayer.motionY = 0.0D;
        mc.gameSettings.keyBindForward.pressed = false;
        if (this.timeHelper.hasReached(this.packetdelay.getValueLong() * 100L)) {
          mc.thePlayer.motionY = -0.2345656D;
          mc.thePlayer.setPosition(mc.thePlayer.posX + MoveUtil.yawPos(this.tplenght.getValue().doubleValue())[0], mc.thePlayer.posY, mc.thePlayer.posZ + MoveUtil.yawPos(this.tplenght.getValue().doubleValue())[1]);
          this.timeHelper.reset();
        } 
        break;
      case "Damage":
        if (!this.damaged) {
          PacketUtil.sendPacketSilent((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.035D, mc.thePlayer.posZ, false));
          PacketUtil.sendPacketSilent((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
          PacketUtil.sendPacketSilent((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
          this.damaged = true;
        } 
        if (this.damaged) {
          mc.thePlayer.motionY = 0.0D;
          MoveUtil.setSpeed(this.speed.getValue().doubleValue());
          mc.timer.timerSpeed = this.timer.getValueInt();
        } 
        break;
      case "Matrix":
        switch (this.matrixmode.getValue()) {
          case "Glide":
            if (!mc.thePlayer.onGround) {
              mc.timer.timerSpeed = (mc.thePlayer.ticksExisted % 4 == 0) ? this.boost.getValueFloat() : 0.06F;
              if (this.timeHelper.hasReached(this.delay)) {
                mc.thePlayer.motionY = -0.005D;
                this.delay = (int)(this.delay + RandomUtil.getRandomGaussian(10.0D));
                this.delay = Math.max(this.delay, 1000);
                this.timeHelper.reset();
              } 
              break;
            } 
            if (event.isPre() && mc.thePlayer.onGround)
              mc.thePlayer.jump(); 
            break;
          case "Blink":
            mc.thePlayer.motionY = 0.0D;
            mc.timer.timerSpeed = 2.0F;
            break;
        } 
        break;
      case "Hycraft":
        switch (this.hycraftMode.getValue()) {
          case "Clip":
            mc.thePlayer.motionY = 0.0D;
            MoveUtil.setSpeed(2.0D);
            mc.timer.timerSpeed = 0.25F;
            if (mc.thePlayer.ticksExisted % 6 == 0)
              PacketUtil.sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 50.0D, mc.thePlayer.posZ, false)); 
            break;
          case "Blink":
            if (this.ticks > 0) {
              mc.thePlayer.motionY = 0.0D;
              mc.thePlayer.onGround = true;
              MoveUtil.setSpeed(0.25D);
            } 
            if (this.ticks == 3)
              PacketUtil.sendFunnyPacket(); 
            this.ticks++;
            break;
        } 
        break;
      case "Motion":
        mc.thePlayer.motionY = 0.0D;
        if (mc.gameSettings.keyBindJump.pressed)
          mc.thePlayer.motionY += this.speed.getValue().doubleValue() / 2.0D; 
        if (mc.gameSettings.keyBindSneak.pressed)
          mc.thePlayer.motionY -= this.speed.getValue().doubleValue() / 2.0D; 
        break;
      case "Minemora Glide":
        mc.timer.timerSpeed = 0.7F;
        if (mc.thePlayer.isMoving())
          mc.thePlayer.motionY = -0.0784000015258789D; 
        break;
      case "Jump":
        if (event.isPre()) {
          if (mc.thePlayer.onGround)
            mc.thePlayer.jump(); 
          if (mc.thePlayer.fallDistance > 1.0F)
            mc.thePlayer.motionY = -(mc.thePlayer.posY - Math.floor(mc.thePlayer.posY)); 
          if (mc.thePlayer.motionY == 0.0D) {
            mc.thePlayer.jump();
            mc.thePlayer.onGround = true;
            event.setOnGround(true);
            mc.thePlayer.fallDistance = 0.0F;
          } 
        } 
        break;
      case "Collison":
        MoveUtil.strafe(this.speed.getValueFloat());
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
          mc.thePlayer.motionY = 0.0D;
          double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
          double d1 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        } 
        break;
      case "Minemora":
        if (this.stage <= 2) {
          mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.0D + Math.abs(Math.random() / 1000.0D), mc.thePlayer.posZ, false));
          mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + Math.abs(Math.random() / 1000.0D), mc.thePlayer.posZ, false));
          mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + Math.abs(Math.random() / 1000.0D), mc.thePlayer.posZ, true));
          mc.thePlayer.jump();
          ChatUtil.sendChatMessageWPrefix("Doing the funny");
          this.stage++;
          break;
        } 
        this.damaged = true;
        mc.thePlayer.motionY = 0.0D;
        if (mc.gameSettings.keyBindJump.pressed)
          mc.thePlayer.motionY += this.speed.getValue().doubleValue() / 2.0D; 
        if (mc.gameSettings.keyBindSneak.pressed)
          mc.thePlayer.motionY -= this.speed.getValue().doubleValue() / 2.0D; 
        break;
    } 
  }
  
  @EventTarget
  public void onMove(EventMove event) {
    switch (this.mode.getValue()) {
      case "Minemora":
        if (this.damaged)
          MoveUtil.setSpeed(event, this.speed.getValue().doubleValue()); 
        break;
      case "Motion":
        MoveUtil.setSpeed(event, this.speed.getValue().doubleValue());
        break;
    } 
  }
  
  @EventTarget
  public void onPacket(EventReceivePacket event) {
    this.mode.getValue().getClass();
    this.mode.getValue();
  }
  
  @EventTarget
  public void onPacketSend(EventSendPacket event) {
    if (((Disabler)ModuleManager.getModuleByName("Disabler")).mode.is("Minebox") && ModuleManager.getModuleByName("Disabler").isEnabled())
      if (event.getPacket() instanceof C03PacketPlayer)
        event.setCancelled(true);  
    switch (this.mode.getValue()) {
      case "Hycraft":
        switch (this.hycraftMode.getValue()) {
          case "Blink":
            if (event.getPacket() instanceof C03PacketPlayer) {
              this.blinkPackets.add(event.getPacket());
              event.setCancelled(true);
            } 
            break;
        } 
        break;
      case "Matrix":
        switch (this.matrixmode.getValue()) {
          case "Blink":
            if (event.getPacket() instanceof C03PacketPlayer) {
              this.blinkPackets.add(event.getPacket());
              event.setCancelled(true);
            } 
            break;
        } 
        break;
      case "Minemora":
        if (event.getPacket() instanceof C03PacketPlayer) {
          this.blinkPackets.add(event.getPacket());
          event.setCancelled(true);
        } 
        break;
    } 
  }
  
  @EventTarget
  public void onCollide(EventCollide event) {
    switch (this.mode.getValue()) {
      case "Collison":
      case "Verus":
        if (event.getBlock() instanceof net.minecraft.block.BlockAir && event.getY() < mc.thePlayer.posY)
          event.setAxisAlignedBB(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1.0D, mc.thePlayer.posY, event.getZ() + 1.0D)); 
        break;
    } 
  }
  
  @EventTarget
  public void onRender(Event2D event) {}
  
  public void onEnable() {
    this.ticks = 0;
    this.i = 0;
    this.damaged = false;
    this.blinkPackets.clear();
    this.can = false;
    if (this.mode.is("Slime")) {
      for (int i = 36; i < 45; i++) {
        ItemStack stack = (getPlayer()).inventoryContainer.getSlot(i).getStack();
        if (stack != null) {
          Item item = stack.getItem();
          if (item instanceof ItemBlock) {
            ItemBlock block = (ItemBlock)item;
            if (Block.getBlockFromItem(item) instanceof net.minecraft.block.BlockSlime)
              mc.thePlayer.inventory.currentItem = i - 36; 
          } 
        } 
      } 
      this.start = mc.thePlayer.ticksExisted;
      this.slime = true;
      mc.thePlayer.motionY = 0.42D;
    } 
    if (this.mode.getValue().equalsIgnoreCase("Bow")) {
      this.damaged = false;
      if (!mc.thePlayer.inventory.hasItem(Items.arrow)) {
        toggle();
        return;
      } 
      ItemStack itemStack = null;
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
    } 
    super.onEnable();
  }
  
  public void onDisable() {
    this.stage = 0;
    this.damaged = false;
    mc.timer.timerSpeed = 1.0F;
    if (this.mode.is("Minemora") || (this.mode.is("Matrix") && this.matrixmode.is("Blink")) || (this.mode.is("Hycraft") && this.hycraftMode.is("Blink")) || this.mode.is("Minemora")) {
      mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0D;
      this.blinkPackets.forEach(packet -> mc.getNetHandler().sendPacketNoEvent(packet));
      this.blinkPackets.clear();
    } 
    setSpeed(0.0D);
    (getPlayer()).capabilities.isFlying = false;
    (getPlayer()).capabilities.setFlySpeed(0.05F);
    MoveUtil.strafe(0.0D);
    if (((Disabler)ModuleManager.getModuleByName("Disabler")).mode.getValue().equalsIgnoreCase("Minebox") && ModuleManager.getModuleByName("Disabler").isEnabled())
      mc.thePlayer.motionY = 0.0D; 
    super.onDisable();
  }
  
  public static double nextDouble(double max, double min) {
    return Math.random() * (max - min) + min;
  }
}
