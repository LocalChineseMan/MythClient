package com.viaversion.viaversion.libs.kyori.adventure.key;

import com.viaversion.viaversion.libs.kyori.examination.Examinable;
import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Key extends Comparable<Key>, Examinable {
  public static final String MINECRAFT_NAMESPACE = "minecraft";
  
  @NotNull
  static Key key(@NotNull @Pattern("([a-z0-9_\\-.]+:)?[a-z0-9_\\-./]+") String string) {
    return key(string, ':');
  }
  
  @NotNull
  static Key key(@NotNull String string, char character) {
    int index = string.indexOf(character);
    String namespace = (index >= 1) ? string.substring(0, index) : "minecraft";
    String value = (index >= 0) ? string.substring(index + 1) : string;
    return key(namespace, value);
  }
  
  @NotNull
  static Key key(@NotNull Namespaced namespaced, @NotNull @Pattern("[a-z0-9_\\-./]+") String value) {
    return key(namespaced.namespace(), value);
  }
  
  @NotNull
  static Key key(@NotNull @Pattern("[a-z0-9_\\-.]+") String namespace, @NotNull @Pattern("[a-z0-9_\\-./]+") String value) {
    return new KeyImpl(namespace, value);
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("namespace", namespace()), 
          ExaminableProperty.of("value", value()) });
  }
  
  default int compareTo(@NotNull Key that) {
    int value = value().compareTo(that.value());
    if (value != 0)
      return KeyImpl.clampCompare(value); 
    return KeyImpl.clampCompare(namespace().compareTo(that.namespace()));
  }
  
  @NotNull
  String namespace();
  
  @NotNull
  String value();
  
  @NotNull
  String asString();
}
