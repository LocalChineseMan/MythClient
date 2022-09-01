package notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl;

import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;

public abstract class ExpandableComponent extends Component {
  private boolean expanded;
  
  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }
  
  public boolean isExpanded() {
    return this.expanded;
  }
  
  public ExpandableComponent(Component parent, String name, int x, int y, int width, int height) {
    super(parent, name, x, y, width, height);
  }
  
  public void onMouseClick(int mouseX, int mouseY, int button) {
    if (isHovered(mouseX, mouseY)) {
      onClick(mouseX, mouseY, button);
      if (canExpand() && button == 1)
        this.expanded = !this.expanded; 
    } 
    if (isExpanded())
      super.onMouseClick(mouseX, mouseY, button); 
  }
  
  public abstract boolean canExpand();
  
  public abstract int getExpandedHeight();
  
  public abstract void onClick(int paramInt1, int paramInt2, int paramInt3);
}
