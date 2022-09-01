package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface BinaryTag extends BinaryTagLike, Examinable {
  @NotNull
  BinaryTagType<? extends BinaryTag> type();
  
  @NotNull
  default BinaryTag asBinaryTag() {
    return this;
  }
}
