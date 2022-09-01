package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.panel;

import net.minecraft.client.gui.ScaledResolution;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;

public abstract class DraggablePanel extends ExpandableComponent {
  private boolean dragging;
  
  private int prevX;
  
  private int prevY;
  
  public DraggablePanel(Component parent, String name, int x, int y, int width, int height) {
    super(parent, name, x, y, width, height);
    this.prevX = x;
    this.prevY = y;
  }
  
  public void renderComponent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
    if (this.dragging) {
      setX(mouseX - this.prevX);
      setY(mouseY - this.prevY);
    } 
  }
  
  public void onClick(int mouseX, int mouseY, int button) {
    if (button == 0) {
      this.dragging = true;
      this.prevX = mouseX - getX();
      this.prevY = mouseY - getY();
    } 
  }
  
  public void onMouseRelease(int button) {
    super.onMouseRelease(button);
    this.dragging = false;
  }
}
