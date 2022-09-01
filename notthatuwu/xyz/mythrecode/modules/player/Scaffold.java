package notthatuwu.xyz.mythrecode.modules.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NoteSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.RandomUtil;
import notthatuwu.xyz.mythrecode.api.utils.RotationUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.EventClickBlock;
import notthatuwu.xyz.mythrecode.events.EventMoveRelative;
import notthatuwu.xyz.mythrecode.events.EventSafeWalk;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "Scaffold", category = Category.PLAYER)
public class Scaffold extends Module {
  public NoteSetting common_settings = new NoteSetting("Common Settings", (Module)this);
  
  public AddonSetting addons = new AddonSetting("Move Addons", (Module)this, "Delay", new String[] { "Strafe", "Delay", "Custom Speed", "Custom Timer", "Safewalk", "KeepY", "Tower", "MoveFix" });
  
  public AddonSetting itemAddons = new AddonSetting("Item Addons", (Module)this, "Pick Block", new String[] { "Swing", "Pick Block" });
  
  public ModeSetting swingmode = new ModeSetting("Swing Mode", (Module)this, new String[] { "Normal", "Packet" }, "Normal", () -> Boolean.valueOf(this.itemAddons.isEnabled("Swing")));
  
  public ModeSetting picker = new ModeSetting("Pick Mode", (Module)this, new String[] { "Silent", "Switch" }, "Silent", () -> Boolean.valueOf(this.itemAddons.isEnabled("Pick Block")));
  
  public ModeSetting rotationMode = new ModeSetting("Rotations", (Module)this, new String[] { "Free", "Calculated", "None" }, "Calculated");
  
  public ModeSetting placeMode = new ModeSetting("Place Mode", (Module)this, new String[] { "Pre", "Post", "Dev" }, "Post");
  
  public ModeSetting sprintBypass = new ModeSetting("Sprint", (Module)this, new String[] { "Spoof", "Normal", "None", "Packet" }, "None");
  
  public ModeSetting eagleMode = new ModeSetting("Eagle", (Module)this, new String[] { "Vulcan", "Air", "None" }, "None");
  
  public ModeSetting towerBypass = new ModeSetting("Tower", (Module)this, new String[] { "NCP" }, "NCP", () -> Boolean.valueOf(this.addons.isEnabled("Tower")));
  
  public BooleanSetting edge = new BooleanSetting("Edge", (Module)this, true);
  
  public BooleanSetting autojump = new BooleanSetting("Auto Jump", (Module)this, false, () -> Boolean.valueOf(this.addons.isEnabled("KeepY")));
  
  public BooleanSetting hideJumps = new BooleanSetting("Hide Jumps", (Module)this, false, () -> Boolean.valueOf((this.autojump.getValue().booleanValue() && this.addons.isEnabled("KeepY"))));
  
  public NumberSetting delay = new NumberSetting("Delay", (Module)this, 150.0D, 0.0D, 1000.0D, true, () -> Boolean.valueOf(this.addons.isEnabled("Delay")));
  
  public NumberSetting customSpeed = new NumberSetting("Custom Speed", (Module)this, 0.5D, 0.1D, 5.0D, false, () -> Boolean.valueOf(this.addons.isEnabled("Custom Speed")));
  
  public NumberSetting customTimer = new NumberSetting("Custom Timer", (Module)this, 1.0D, 0.1D, 2.5D, false, () -> Boolean.valueOf(this.addons.isEnabled("Custom Timer")));
  
  public NumberSetting towerTimerBoost = new NumberSetting("Tower Timer Boost", (Module)this, 1.0D, 0.1D, 2.5D, false, () -> Boolean.valueOf(this.addons.isEnabled("Custom Timer")));
  
  public List<Block> validBlocks = Arrays.asList(new Block[] { Blocks.air, (Block)Blocks.water, (Block)Blocks.flowing_water, (Block)Blocks.lava, (Block)Blocks.flowing_lava });
  
  public BlockPos[] blockPositions = new BlockPos[] { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
  
  public EnumFacing[] facings = new EnumFacing[] { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH };
  
  private int slot;
  
  private int startY;
  
  private BlockPos currentPos;
  
  private EnumFacing currentFacing;
  
  private TimeHelper timeHelper = new TimeHelper();
  
  private double keepYPos;
  
  private int vulcanBlockEagle;
  
  private int towerBlock;
  
  private BlockData data;
  
  private int silentSlot;
  
  @EventTarget
  public void onMotion(EventUpdate event) {
    setSuffix(this.addons.isEnabled("Delay") ? Integer.valueOf(this.delay.getValueInt()) : "");
    if (mc.thePlayer.inventory.currentItem < 0)
      mc.thePlayer.inventory.currentItem = 0; 
    if (event.isPre()) {
      if (this.autojump.getValue().booleanValue() && this.addons.isEnabled("KeepY") && this.hideJumps.getValue().booleanValue()) {
        Entity ent = mc.getRenderViewEntity();
        double y = mc.thePlayer.posY - this.startY;
        (mc.getRenderViewEntity()).posY -= y;
      } 
      if (this.addons.isEnabled("KeepY") && this.autojump.getValue().booleanValue() && mc.thePlayer.onGround && mc.thePlayer.isMoving())
        mc.thePlayer.jump(); 
    } 
    if (this.addons.isEnabled("Custom Speed"))
      MoveUtil.setSpeed(this.customSpeed.getValue().doubleValue() / 3.5D); 
    if (this.addons.isEnabled("Custom Timer"))
      mc.timer.timerSpeed = this.customTimer.getValueFloat(); 
    if (this.addons.isEnabled("Strafe"))
      MoveUtil.strafe(); 
    this.silentSlot = getSlotWithBlock();
    if (this.picker.is("Silent")) {
      mc.thePlayer.sendQueue.addToSendQueue((Packet)new C09PacketHeldItemChange(this.silentSlot));
    } else {
      mc.thePlayer.inventory.currentItem = this.silentSlot;
    } 
    switch (this.sprintBypass.getValue()) {
      case "None":
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        break;
      case "Packet":
        mc.thePlayer.sendQueue.sendPacketNoEvent((Packet)new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        break;
      case "Normal":
        if (event.isPre())
          mc.thePlayer.setSprinting(true); 
        break;
    } 
    double yDif = 1.0D;
    float blockPlace = 1.0F;
    double posY;
    for (posY = mc.thePlayer.posY - blockPlace; posY > 0.0D; posY--) {
      BlockData newData = null;
      if (this.addons.isEnabled("KeepY")) {
        if (!mc.thePlayer.isMoving())
          this.keepYPos = mc.thePlayer.posY; 
        newData = getBlockData(new BlockPos(mc.thePlayer.posX, this.keepYPos - 1.0D, mc.thePlayer.posZ));
      } else {
        newData = getBlockData(new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ));
      } 
      if (newData != null) {
        yDif = mc.thePlayer.posY - posY;
        if (yDif <= 4.0D) {
          this.data = newData;
          break;
        } 
      } 
    } 
    try {
      BlockPos pos = this.data.pos;
      this.currentPos = pos;
      this.currentFacing = this.data.face;
      Block block = mc.theWorld.getBlockState(pos.offset(this.data.face)).getBlock();
      Vec3 hitVec = getVec3(this.data);
      float[] rots = RotationUtil.faceBlock(this.data.pos);
      if (this.rotationMode.getValue().equalsIgnoreCase("Free")) {
        event.setYaw(mc.thePlayer.rotationYaw - 180.0F);
        event.setPitch(82.04998F);
      } else if (this.rotationMode.getValue().equalsIgnoreCase("Calculated")) {
        event.setYaw(rots[0]);
        event.setPitch(rots[1]);
      } 
      switch (this.placeMode.getValue()) {
        case "Pre":
          if (event.isPre())
            placeBlock(pos, this.data, yDif, block, hitVec); 
          break;
        case "Post":
          if (event.isPost())
            placeBlock(pos, this.data, yDif, block, hitVec); 
          break;
      } 
    } catch (Exception exception) {}
  }
  
  @EventTarget
  public void onSendPacket(EventSendPacket event) {
    switch (this.sprintBypass.getValue()) {
      case "Un Sprint":
      case "Spoof":
        if (event.getPacket() instanceof C0BPacketEntityAction) {
          C0BPacketEntityAction packet = (C0BPacketEntityAction)event.getPacket();
          if (packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING)
            event.setCancelled(true); 
          if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)
            event.setCancelled(true); 
        } 
        break;
    } 
  }
  
  @EventTarget
  public void onMoveRelative(EventMoveRelative event) {
    if (this.addons.isEnabled("MoveFix"))
      event.setCancelled(true); 
  }
  
  @EventTarget
  public void onSafe(EventSafeWalk event) {
    if (this.addons.isEnabled("Safewalk"))
      event.setSafe(true); 
  }
  
  @EventTarget
  public void onClickBlock(EventClickBlock event) {
    if (this.placeMode.getValue().equalsIgnoreCase("Dev")) {
      BlockPos pos = this.data.pos;
      this.currentPos = pos;
      this.currentFacing = this.data.face;
      Block block = mc.theWorld.getBlockState(pos.offset(this.data.face)).getBlock();
      Vec3 hitVec = getVec3(this.data);
      placeBlock(pos, this.data, 1.0D, block, hitVec);
    } 
  }
  
  private void placeBlock(BlockPos pos, BlockData data, double yDif, Block block, Vec3 hitVec) {
    if (data != null && isOnEdgeWithOffset(this.edge.getValue().booleanValue() ? 0.20000000298023224D : 0.0D)) {
      if (!this.validBlocks.contains(block) || isBlockUnder(yDif))
        return; 
      if (this.eagleMode.getValue().equalsIgnoreCase("Air"))
        if (block.getMaterial() == Material.air) {
          setSneaking(true);
        } else {
          setSneaking(false);
        }  
      if (this.addons.isEnabled("Delay") && !this.timeHelper.hasReached(this.delay.getValueLong()))
        return; 
      if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(this.silentSlot), pos, data.face, hitVec)) {
        if (this.addons.isEnabled("Tower")) {
          int y;
          if (this.addons.isEnabled("Custom Timer"))
            mc.timer.timerSpeed = this.towerTimerBoost.getValueInt(); 
          this.towerBlock++;
          switch (this.towerBypass.getValue()) {
            case "NCP":
              if (!mc.gameSettings.keyBindJump.isKeyDown() || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() instanceof net.minecraft.block.BlockAir)
                break; 
              y = (int)mc.thePlayer.posY;
              if (mc.thePlayer.posY - y < 0.05D) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
                mc.thePlayer.motionY = 0.42D;
              } 
              if (mc.thePlayer.posY - y < 0.75D)
                break; 
              mc.thePlayer.motionY = 0.25D;
              break;
          } 
        } 
        if (this.eagleMode.getValue().equalsIgnoreCase("Vulcan")) {
          this.vulcanBlockEagle++;
          if (this.vulcanBlockEagle >= (this.sprintBypass.getValue().equalsIgnoreCase("Un Sprint") ? 5 : 3)) {
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            this.vulcanBlockEagle = 0;
          } else if (mc.thePlayer.isSneaking()) {
          
          } 
        } 
        if (this.itemAddons.isEnabled("Swing"))
          if (this.swingmode.is("Normal")) {
            mc.thePlayer.swingItem();
          } else {
            PacketUtil.sendPacket((Packet)new C0APacketAnimation());
          }  
      } 
      this.timeHelper.reset();
      if (this.eagleMode.getValue().equalsIgnoreCase("Air") && 
        mc.thePlayer.isSneaking())
        setSneaking(false); 
      if (this.eagleMode.getValue().equalsIgnoreCase("Vulcan"));
    } 
  }
  
  private boolean isOnEdgeWithOffset(double paramDouble) {
    double d1 = mc.thePlayer.posX;
    double d2 = mc.thePlayer.posY;
    double d3 = mc.thePlayer.posZ;
    BlockPos blockPos1 = new BlockPos(d1 - paramDouble, d2 - 0.5D, d3 - paramDouble);
    BlockPos blockPos2 = new BlockPos(d1 - paramDouble, d2 - 0.5D, d3 + paramDouble);
    BlockPos blockPos3 = new BlockPos(d1 + paramDouble, d2 - 0.5D, d3 + paramDouble);
    BlockPos blockPos4 = new BlockPos(d1 + paramDouble, d2 - 0.5D, d3 - paramDouble);
    return (mc.thePlayer.worldObj.getBlockState(blockPos1).getBlock() == Blocks.air && mc.thePlayer.worldObj.getBlockState(blockPos2).getBlock() == Blocks.air && mc.thePlayer.worldObj.getBlockState(blockPos3).getBlock() == Blocks.air && mc.thePlayer.worldObj.getBlockState(blockPos4).getBlock() == Blocks.air);
  }
  
  private float[] getRotationByVec(Vec3 hitVec) {
    double d1 = this.currentPos.getX() + 0.5D - mc.thePlayer.posX + (this.currentFacing.getFrontOffsetX() / 2.0F);
    double d2 = 60.0D - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + (this.currentFacing.getFrontOffsetY() / 2.0F);
    double d3 = this.currentPos.getZ() + 0.5D - mc.thePlayer.posZ + (this.currentFacing.getFrontOffsetZ() / 2.0F);
    double d4 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
    float f1 = (float)(Math.atan2(d3, d1) * 180.0D / Math.PI) - 90.0F;
    float f2 = (float)-(Math.atan2(d2, d4) * 180.0D / Math.PI);
    return new float[] { f1, 80.0F };
  }
  
  private int getSlotWithBlock() {
    for (int i = 0; i < 9; ) {
      ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
      if (itemStack == null || !(itemStack.getItem() instanceof net.minecraft.item.ItemBlock) || itemStack.stackSize == 0 || !isGoodBlockStack(itemStack)) {
        i++;
        continue;
      } 
      return i;
    } 
    return 0;
  }
  
  private boolean isBlockUnder(double yOffset) {
    EntityPlayerSP player = mc.thePlayer;
    return 
      !this.validBlocks.contains(mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - yOffset, player.posZ)).getBlock());
  }
  
  private Vec3 getVec3(BlockData data) {
    BlockPos pos = data.pos;
    EnumFacing face = data.face;
    double x = pos.getX() + 0.5D;
    double y = pos.getY() + 0.5D;
    double z = pos.getZ() + 0.5D;
    x += face.getFrontOffsetX() / 2.0D;
    z += face.getFrontOffsetZ() / 2.0D;
    y += face.getFrontOffsetY() / 2.0D;
    if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
      x += RandomUtil.nextDouble(0.3D, -0.3D);
      z += RandomUtil.nextDouble(0.3D, -0.3D);
    } else {
      y += RandomUtil.nextDouble(0.49D, 0.5D);
    } 
    if (face == EnumFacing.WEST || face == EnumFacing.EAST)
      z += RandomUtil.nextDouble(0.3D, -0.3D); 
    if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH)
      x += RandomUtil.nextDouble(0.3D, -0.3D); 
    return new Vec3(x, y, z);
  }
  
  final EnumFacing[] invert = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST };
  
  private BlockData getBlockData(BlockPos pos) {
    for (EnumFacing facingVal : EnumFacing.values()) {
      BlockPos offset = pos.offset(facingVal);
      if (isValidBlock(mc.theWorld.getBlockState(offset).getBlock(), false))
        return new BlockData(offset, this.invert[facingVal.ordinal()]); 
    } 
    for (EnumFacing face : EnumFacing.values()) {
      BlockPos offsetPos = pos.offset(face, 1);
      for (EnumFacing face2 : EnumFacing.values()) {
        if (face2 != EnumFacing.DOWN && face2 != EnumFacing.UP) {
          BlockPos offset = offsetPos.offset(face2);
          if (isValidBlock(mc.theWorld.getBlockState(offset).getBlock(), false))
            return new BlockData(offset, this.invert[face2.ordinal()]); 
        } 
      } 
    } 
    return null;
  }
  
  public static boolean isValidBlock(Block block, boolean toPlace) {
    if (block instanceof net.minecraft.block.BlockContainer)
      return false; 
    if (toPlace)
      return (!(block instanceof net.minecraft.block.BlockFalling) && block.isFullBlock() && block.isFullCube()); 
    Material material = block.getMaterial();
    return (!material.isReplaceable() && !material.isLiquid());
  }
  
  public static boolean isGoodBlockStack(ItemStack stack) {
    return (stack.stackSize >= 1 && isValidBlock(Block.getBlockFromItem(stack.getItem()), true));
  }
  
  private void setSneaking(boolean b) {
    KeyBinding sneakBinding = mc.gameSettings.keyBindSneak;
    mc.gameSettings.keyBindSneak.pressed = b;
  }
  
  private static class BlockData {
    public final BlockPos pos;
    
    public final EnumFacing face;
    
    private BlockData(BlockPos pos, EnumFacing face) {
      this.pos = pos;
      this.face = face;
    }
  }
  
  public void onEnable() {
    this.slot = mc.thePlayer.inventory.currentItem;
    this.startY = (int)mc.thePlayer.posY;
    this.keepYPos = mc.thePlayer.posY;
    this.towerBlock = 0;
    this.timeHelper.reset();
    if (this.sprintBypass.getValue().equalsIgnoreCase("Spoof"))
      mc.thePlayer.sendQueue.sendPacketNoEvent((Packet)new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING)); 
    super.onEnable();
  }
  
  public void onDisable() {
    setSneaking(false);
    mc.timer.timerSpeed = 1.0F;
    if (this.picker.is("Silent")) {
      PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(this.slot));
    } else {
      mc.thePlayer.inventory.currentItem = this.slot;
    } 
    if (this.eagleMode.getValue().equalsIgnoreCase("vulcan") && this.vulcanBlockEagle >= (this.sprintBypass.getValue().equalsIgnoreCase("Un Sprint") ? 5 : 3))
      mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING)); 
    super.onDisable();
  }
}
