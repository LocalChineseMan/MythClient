package notthatuwu.xyz.mythrecode.modules.player;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "Breaker", category = Category.PLAYER)
public class Breaker extends Module {
  public ModeSetting type = new ModeSetting("Type", this, new String[] { "Instant" }, "Instant");
  
  public NumberSetting radius = new NumberSetting("Radius", this, 3.0D, 1.0D, 6.0D, true);
  
  private int xPos;
  
  private int yPos;
  
  private int zPos;
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    switch (this.type.getValue()) {
      case "Instant":
        if (event.isPre())
          for (int x = -this.radius.getValueInt(); x < this.radius.getValueInt(); x++) {
            for (Double y = this.radius.getValue(); y.doubleValue() > -this.radius.getValueInt(); double_1 = y, double_2 = y = Double.valueOf(y.doubleValue() - 1.0D)) {
              Double double_1;
              Double double_2;
              for (int z = -this.radius.getValueInt(); z < this.radius.getValueInt(); z++) {
                this.xPos = (int)(mc.thePlayer.posX + x);
                this.yPos = (int)(mc.thePlayer.posY + y.doubleValue());
                this.zPos = (int)(mc.thePlayer.posZ + z);
                BlockPos bP = new BlockPos(this.xPos, this.yPos, this.zPos);
                Block b = mc.theWorld.getBlockState(bP).getBlock();
                if (b.getBlockState().getBlock() == Block.getBlockById(26)) {
                  mc.thePlayer.swingItem();
                  mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bP, EnumFacing.NORTH));
                  mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bP, EnumFacing.NORTH));
                } 
              } 
            } 
          }  
        break;
    } 
  }
}
