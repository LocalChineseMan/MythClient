package notthatuwu.xyz.mythrecode.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.PlayerUtil;
import notthatuwu.xyz.mythrecode.api.utils.RandomUtil;
import notthatuwu.xyz.mythrecode.api.utils.RotationUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.math.Vec3d;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventStrafe;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.player.Teams;
import optifine.MathUtils;

@Info(name = "KillAura", category = Category.COMBAT)
public class KillAura extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Single", "Switch" }, "Single");
  
  public AddonSetting addons = new AddonSetting("Addons", this, "Rotations", new String[] { "Rotations", "Auto Block", "Sprint Bypass", "No Sprint", "Only Air", "Legit", "Move Fix" });
  
  public ModeSetting rotations = new ModeSetting("Rotations", this, new String[] { "Smooth", "Random" }, "Smooth", () -> Boolean.valueOf(this.addons.isEnabled("Rotations")));
  
  public ModeSetting sortingMode = new ModeSetting("Sorting Mode", this, new String[] { "Angle", "Distance", "Health" }, "Health");
  
  public ModeSetting autoBlockMode = new ModeSetting("Block Mode", this, new String[] { "Normal", "RightClick", "Interact", "Fake" }, "Fake", () -> Boolean.valueOf(this.addons.isEnabled("Auto Block")));
  
  public ModeSetting autoBlockType = new ModeSetting("Block Type", this, new String[] { "Pre", "Post" }, "Pre", () -> Boolean.valueOf(this.addons.isEnabled("Auto Block")));
  
  public AddonSetting targetsaddon = new AddonSetting("Targets", this, "Player", new String[] { "Player", "Animals", "Mobs", "Invisibles", "Wall" });
  
  public NumberSetting smoothnes = new NumberSetting("Smoothness", this, 60.0D, 0.0D, 100.0D, false, () -> Boolean.valueOf(this.rotations.is("Smooth")));
  
  public NumberSetting randomization = new NumberSetting("Yaw Random", this, 0.0D, 0.0D, 25.0D, false, () -> Boolean.valueOf(this.rotations.is("Random")));
  
  public NumberSetting randomization2 = new NumberSetting("Pitch Random", this, 0.0D, 0.0D, 25.0D, false, () -> Boolean.valueOf(this.rotations.is("Random")));
  
  public NumberSetting minCps = new NumberSetting("Min Cps", this, 7.0D, 1.0D, 20.0D, false);
  
  public NumberSetting maxCps = new NumberSetting("Max Cps", this, 8.0D, 1.0D, 20.0D, false);
  
  public NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 100.0D, 1.0D, 1000.0D, true, () -> Boolean.valueOf(this.mode.getValue().equalsIgnoreCase("Switch")));
  
  public NumberSetting range = new NumberSetting("Attack Range", this, 4.0D, 1.0D, 7.0D, false);
  
  public NumberSetting autoBlockRange = new NumberSetting("Block Range", this, 4.0D, 1.0D, 7.0D, false);
  
  public BooleanSetting autoJump = new BooleanSetting("Auto Jump", this, false, () -> Boolean.valueOf(this.addons.isEnabled("Only Air")));
  
  public ArrayList<EntityLivingBase> targets = new ArrayList<>();
  
  public static EntityLivingBase target;
  
  public int targetIndex;
  
  public TimeHelper switchTimeHelper = new TimeHelper();
  
  public TimeHelper attackTimeHelper = new TimeHelper();
  
  public static boolean blocking;
  
  private boolean sprintSpoof;
  
  private RotationUtil.Rotation serverRotation = new RotationUtil.Rotation(0.0F, 0.0F);
  
  private final RotationUtil.Rotation lastRotation = new RotationUtil.Rotation(0.0F, 0.0F);
  
  private final RotationUtil.Rotation rotation = new RotationUtil.Rotation(0.0F, 0.0F);
  
  private Random RANDOM = new Random();
  
  private long attackMs;
  
  private static float iyaw;
  
  private static float ipitch;
  
  @EventTarget
  public void onStrafe(EventStrafe event) {
    if (this.addons.isEnabled("Move Fix") && this.addons.isEnabled("Rotation"))
      event.setYaw(this.rotation.getYaw()); 
  }
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    setSuffix(this.mode.getValue());
    getAllTarget();
    sortTargets();
    slotTargetSwitch();
    if (this.addons.isEnabled("No Sprint") && target != null)
      mc.thePlayer.setSprinting(false); 
    if (this.minCps.getValue().doubleValue() > this.maxCps.getValue().doubleValue())
      this.minCps.setValue(this.maxCps.getValue().doubleValue()); 
    if (event.isPre()) {
      if (this.addons.isEnabled("Sprint Bypass") && target == null && this.sprintSpoof)
        this.sprintSpoof = false; 
      if (this.autoBlockType.getValue().equalsIgnoreCase("Pre") && this.addons.isEnabled("Auto Block"))
        block(); 
      if (target != null) {
        if (this.addons.isEnabled("Rotations"))
          if (this.rotations.is("Random")) {
            event.setPitch(RotationUtil.getNeededRotations(target)[1] + MathUtils.randomNumber(this.randomization2.getValueInt(), -this.randomization2.getValueInt()));
            event.setYaw(RotationUtil.getNeededRotations(target)[0] + MathUtils.randomNumber(this.randomization.getValueInt(), -this.randomization.getValueInt()));
          } else if (this.rotations.is("Smooth")) {
            float frac = MathHelper.clamp_float((float)(1.0D - this.smoothnes.getValue().doubleValue() / 100.0D), 0.1F, 1.0F);
            float[] smooth = RotationUtil.getAngles(target);
            iyaw += (smooth[0] - iyaw) * frac;
            ipitch += (smooth[1] - ipitch) * frac;
            event.setYaw(iyaw);
            event.setPitch(ipitch);
          }  
        if (this.addons.isEnabled("Sprint Bypass") && !this.sprintSpoof && !this.addons.isEnabled("No Sprint")) {
          if (mc.thePlayer.isSprinting())
            mc.thePlayer.sendQueue.sendPacketNoEvent((Packet)new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING)); 
          this.sprintSpoof = true;
        } 
      } 
      if (this.addons.isEnabled("Only Air") && this.autoJump.getValue().booleanValue() && target != null && 
        mc.thePlayer.onGround) {
        mc.gameSettings.keyBindJump.pressed = false;
        mc.thePlayer.jump();
      } 
      if (this.attackTimeHelper.hasReached(this.attackMs) && target != null && mc.thePlayer.getDistanceToEntity((Entity)target) <= this.range.getValue().doubleValue()) {
        this.attackMs = 1000L / (int)(RandomUtil.nextDouble(this.maxCps.getValue().doubleValue(), this.minCps.getValue().doubleValue()) - this.RANDOM.nextInt(10) + this.RANDOM.nextInt(10));
        attack(target, this.addons.isEnabled("Legit"));
        this.attackTimeHelper.reset();
      } 
      if (this.addons.isEnabled("Auto Block") && this.autoBlockType.getValue().equalsIgnoreCase("Pre"))
        unBlock(); 
    } 
    if (event.isPost() && 
      this.addons.isEnabled("Auto Block") && this.autoBlockType.getValue().equalsIgnoreCase("Post")) {
      block();
      unBlock();
    } 
  }
  
  @EventTarget
  public void onSendPacket(EventSendPacket event) {
    if (this.addons.isEnabled("Sprint Bypass") && target != null && this.sprintSpoof && 
      event.getPacket() instanceof C0BPacketEntityAction) {
      C0BPacketEntityAction packet = (C0BPacketEntityAction)event.getPacket();
      if (packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING)
        event.setCancelled(true); 
      if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)
        event.setCancelled(true); 
    } 
    if (event.getPacket() instanceof C03PacketPlayer) {
      C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
      if (packet.rotating)
        this.serverRotation = new RotationUtil.Rotation(packet.yaw, packet.pitch); 
    } 
  }
  
  private void attack(EntityLivingBase entity, boolean legit) {
    if (ModuleManager.getModuleByName("Scaffold").isEnabled())
      return; 
    if (this.addons.isEnabled("Only Air") && mc.thePlayer.onGround)
      return; 
    if (legit) {
      mc.thePlayer.swingItem();
      mc.playerController.attackEntity((EntityPlayer)mc.thePlayer, (Entity)entity);
    } else {
      mc.thePlayer.swingItem();
      mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
      float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.inventory.getCurrentItem(), entity.getCreatureAttribute());
      if (sharpLevel > 0.0F)
        mc.thePlayer.onEnchantmentCritical((Entity)entity); 
    } 
  }
  
  public void slotTargetSwitch() {
    if (this.switchTimeHelper.hasReached(this.switchDelay.getValueLong()) && this.mode.getValue().equals("Switch")) {
      this.targetIndex++;
      this.switchTimeHelper.reset();
    } 
    if (this.targetIndex >= this.targets.size())
      this.targetIndex = 0; 
    target = (!this.targets.isEmpty() && this.targetIndex < this.targets.size()) ? this.targets.get(this.targetIndex) : null;
  }
  
  private void sortTargets() {
    switch (this.sortingMode.getValue()) {
      case "Angle":
        this.targets.sort(
            Comparator.comparingDouble(RotationUtil::getAngleChange));
        break;
      case "Distance":
        this.targets.sort(
            Comparator.comparingDouble(RotationUtil::getDistanceToEntity));
        break;
      case "Health":
        this.targets.sort(
            Comparator.comparingDouble(EntityLivingBase::getHealth));
        break;
    } 
  }
  
  private void getAllTarget() {
    this.targets.clear();
    for (Entity entity : (mc.thePlayer.getEntityWorld()).loadedEntityList) {
      if (entity instanceof EntityLivingBase) {
        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
        if (isValidEntity(entityLivingBase))
          this.targets.add(entityLivingBase); 
      } 
    } 
  }
  
  private boolean isValidEntity(EntityLivingBase ent) {
    if (mc.thePlayer.isDead)
      return false; 
    if (ent.getHealth() <= 0.0F)
      return false; 
    if (!ent.canEntityBeSeen((Entity)mc.thePlayer) && !this.targetsaddon.isEnabled("Wall"))
      return false; 
    if (ent instanceof net.minecraft.entity.item.EntityArmorStand)
      return false; 
    return (ent != mc.thePlayer && (!(ent instanceof EntityPlayer) || this.targetsaddon.isEnabled("Player")) && ((!(ent instanceof net.minecraft.entity.passive.EntityAnimal) && !(ent instanceof net.minecraft.entity.passive.EntitySquid)) || this.targetsaddon.isEnabled("Animals")) && ((!(ent instanceof net.minecraft.entity.monster.EntityMob) && !(ent instanceof net.minecraft.entity.passive.EntityVillager) && !(ent instanceof net.minecraft.entity.monster.EntitySnowman) && !(ent instanceof net.minecraft.entity.passive.EntityBat)) || this.targetsaddon.isEnabled("Mobs")) && mc.thePlayer.getDistanceToEntity((Entity)ent) <= 
      attackRange(ent) + 0.4D && (!ent.isInvisible() || this.targetsaddon.isEnabled("Invisibles")) && (!ModuleManager.getModuleByName("AntiBot").isEnabled() || !AntiBot.isBot(ent)) && !mc.thePlayer.isDead && (!(ent instanceof EntityPlayer) || !Teams.isOnSameTeam((Entity)ent)));
  }
  
  public void onEnable() {
    this.rotation.setPitch(mc.thePlayer.rotationPitch);
    this.rotation.setYaw(mc.thePlayer.rotationYaw);
    this.lastRotation.setPitch(mc.thePlayer.prevRotationPitch);
    this.lastRotation.setYaw(mc.thePlayer.prevRotationYaw);
    this.attackMs = (long)(Math.random() * (1000.0D / this.minCps.getValue().doubleValue() - 1000.0D / this.maxCps.getValue().doubleValue() + 1.0D) + 1000.0D / this.maxCps.getValue().doubleValue());
    target = null;
    this.targetIndex = 0;
    this.targets.clear();
    this.sprintSpoof = false;
    if (blocking)
      unBlock(); 
    super.onEnable();
  }
  
  public void onDisable() {
    target = null;
    this.targetIndex = 0;
    this.targets.clear();
    if (blocking)
      unBlock(); 
    super.onDisable();
  }
  
  private void block() {
    if (target != null && PlayerUtil.isHoldingSword() && 
      mc.thePlayer.getDistanceToEntity((Entity)target) <= this.autoBlockRange.getValue().doubleValue()) {
      switch (this.autoBlockMode.getValue()) {
        case "Normal":
          mc.thePlayer.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
          mc.thePlayer.sendQueue.getNetworkManager().sendPacket((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
          break;
        case "RightClick":
          mc.gameSettings.keyBindUseItem.pressed = true;
          break;
        case "Interact":
          mc.playerController.interactWithEntitySendPacket((EntityPlayer)mc.thePlayer, (Entity)target);
          mc.thePlayer.sendQueue.addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
          break;
      } 
      blocking = true;
    } 
  }
  
  private void unBlock() {
    if (blocking && (target == null || mc.thePlayer.getDistanceToEntity((Entity)target) > this.autoBlockRange.getValue().doubleValue())) {
      if (PlayerUtil.isHoldingSword())
        switch (this.autoBlockMode.getValue()) {
          case "Normal":
          case "Interact":
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            break;
          case "RightClick":
            mc.gameSettings.keyBindUseItem.pressed = false;
            break;
        }  
      blocking = false;
    } 
  }
  
  private double attackRange(EntityLivingBase target) {
    double distance = 0.0D;
    if (this.addons.isEnabled("Auto Block")) {
      if (this.autoBlockRange.getValue().doubleValue() >= this.range.getValue().doubleValue())
        return this.autoBlockRange.getValue().doubleValue(); 
      distance = this.range.getValue().doubleValue();
    } else {
      distance = this.range.getValue().doubleValue();
    } 
    return distance;
  }
  
  public static float[] getRotations(Entity e) {
    Vec3d eyesPos = new Vec3d((Minecraft.getMinecraft()).thePlayer.posX, (Minecraft.getMinecraft()).thePlayer.posY + (Minecraft.getMinecraft()).thePlayer.getEyeHeight(), (Minecraft.getMinecraft()).thePlayer.posZ);
    AxisAlignedBB bb = e.getEntityBoundingBox();
    Vec3d vec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + (bb.maxY - bb.minY) * 0.8999999761581421D, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    double diffX = vec.xCoord - eyesPos.xCoord;
    double diffY = vec.yCoord - eyesPos.yCoord;
    double diffZ = vec.zCoord - eyesPos.zCoord;
    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
    float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
    float nigger = 0.0F;
    if (mc.thePlayer.ticksExisted % 3 == 0)
      nigger = ThreadLocalRandom.current().nextInt(-3, 1); 
    return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) + nigger };
  }
}
