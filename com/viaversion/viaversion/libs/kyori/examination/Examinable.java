package com.viaversion.viaversion.libs.kyori.examination;

import java.util.stream.Stream;

public interface Examinable {
  default String examinableName() {
    return getClass().getSimpleName();
  }
  
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.empty();
  }
  
  default <R> R examine(Examiner<R> examiner) {
    return (R)examiner.examine(this);
  }
}
