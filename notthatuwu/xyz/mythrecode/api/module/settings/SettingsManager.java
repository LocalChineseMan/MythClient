package notthatuwu.xyz.mythrecode.api.module.settings;

import java.util.ArrayList;
import notthatuwu.xyz.mythrecode.api.module.Module;

public class SettingsManager {
  public ArrayList<Setting> getSettingsFromModule(Module mod) {
    return mod.settings;
  }
}
