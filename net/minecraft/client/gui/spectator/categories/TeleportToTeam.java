package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
  private final List<ISpectatorMenuObject> field_178672_a = Lists.newArrayList();
  
  public TeleportToTeam() {
    Minecraft minecraft = Minecraft.getMinecraft();
    for (ScorePlayerTeam scoreplayerteam : minecraft.theWorld.getScoreboard().getTeams())
      this.field_178672_a.add(new TeamSelectionObject(this, scoreplayerteam)); 
  }
  
  public List<ISpectatorMenuObject> func_178669_a() {
    return this.field_178672_a;
  }
  
  public IChatComponent func_178670_b() {
    return (IChatComponent)new ChatComponentText("Select a team to teleport to");
  }
  
  public void func_178661_a(SpectatorMenu menu) {
    menu.func_178647_a((ISpectatorMenuView)this);
  }
  
  public IChatComponent getSpectatorName() {
    return (IChatComponent)new ChatComponentText("Teleport to team member");
  }
  
  public void func_178663_a(float p_178663_1_, int alpha) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
    Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
  }
  
  public boolean func_178662_A_() {
    for (ISpectatorMenuObject ispectatormenuobject : this.field_178672_a) {
      if (ispectatormenuobject.func_178662_A_())
        return true; 
    } 
    return false;
  }
  
  class TeleportToTeam {}
}
