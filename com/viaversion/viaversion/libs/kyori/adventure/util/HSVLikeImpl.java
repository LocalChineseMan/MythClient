package com.viaversion.viaversion.libs.kyori.adventure.util;

import com.viaversion.viaversion.libs.kyori.examination.Examiner;
import com.viaversion.viaversion.libs.kyori.examination.string.StringExaminer;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

final class HSVLikeImpl implements HSVLike {
  private final float h;
  
  private final float s;
  
  private final float v;
  
  HSVLikeImpl(float h, float s, float v) {
    this.h = h;
    this.s = s;
    this.v = v;
  }
  
  public float h() {
    return this.h;
  }
  
  public float s() {
    return this.s;
  }
  
  public float v() {
    return this.v;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof HSVLikeImpl))
      return false; 
    HSVLikeImpl that = (HSVLikeImpl)other;
    return (ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Float.valueOf(this.h), Float.valueOf(this.s), Float.valueOf(this.v) });
  }
  
  public String toString() {
    return (String)examine((Examiner)StringExaminer.simpleEscaping());
  }
}
