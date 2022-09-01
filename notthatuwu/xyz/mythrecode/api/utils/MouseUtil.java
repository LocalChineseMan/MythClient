package notthatuwu.xyz.mythrecode.api.utils;

public class MouseUtil {
  public static boolean isHovered(int mouseX, int mouseY, double x, double y, double width, double height) {
    return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
  }
}
