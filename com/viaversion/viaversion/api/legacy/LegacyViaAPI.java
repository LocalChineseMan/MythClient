package com.viaversion.viaversion.api.legacy;

import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;

public interface LegacyViaAPI<T> {
  BossBar createLegacyBossBar(String paramString, float paramFloat, BossColor paramBossColor, BossStyle paramBossStyle);
  
  default BossBar createLegacyBossBar(String title, BossColor color, BossStyle style) {
    return createLegacyBossBar(title, 1.0F, color, style);
  }
}
