package notthatuwu.xyz.mythrecode.modules.movement;

import net.minecraft.client.settings.KeyBinding;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import org.lwjgl.input.Keyboard;

@Info(name = "InvMove", category = Category.MOVEMENT)
public class InvMove extends Module {
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (MC.currentScreen == null || MC.currentScreen instanceof net.minecraft.client.gui.GuiChat)
      return; 
    KeyBinding[] key = { (getGameSettings()).keyBindForward, (getGameSettings()).keyBindBack, (getGameSettings()).keyBindLeft, (getGameSettings()).keyBindRight, (getGameSettings()).keyBindSprint, (getGameSettings()).keyBindJump };
    KeyBinding[] array;
    for (int length = (array = key).length, i = 0; i < length; i++) {
      KeyBinding b = array[i];
      KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
    } 
  }
}
