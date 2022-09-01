package com.viaversion.viaversion.libs.kyori.adventure.text.format;

import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface TextDecorationAndState extends Examinable, StyleBuilderApplicable {
  TextDecoration decoration();
  
  TextDecoration.State state();
  
  default void styleApply(Style.Builder style) {
    style.decoration(decoration(), state());
  }
  
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("decoration", decoration()), 
          ExaminableProperty.of("state", state()) });
  }
}
