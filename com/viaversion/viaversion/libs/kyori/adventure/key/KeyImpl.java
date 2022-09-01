package com.viaversion.viaversion.libs.kyori.adventure.key;

import com.viaversion.viaversion.libs.kyori.examination.ExaminableProperty;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

final class KeyImpl implements Key {
  static final String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
  
  static final String VALUE_PATTERN = "[a-z0-9_\\-./]+";
  
  private static final IntPredicate NAMESPACE_PREDICATE;
  
  private static final IntPredicate VALUE_PREDICATE;
  
  private final String namespace;
  
  private final String value;
  
  static {
    NAMESPACE_PREDICATE = (value -> (value == 95 || value == 45 || (value >= 97 && value <= 122) || (value >= 48 && value <= 57) || value == 46));
    VALUE_PREDICATE = (value -> (value == 95 || value == 45 || (value >= 97 && value <= 122) || (value >= 48 && value <= 57) || value == 47 || value == 46));
  }
  
  KeyImpl(@NotNull String namespace, @NotNull String value) {
    if (!namespaceValid(namespace))
      throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9_.-] character in namespace of Key[%s]", new Object[] { asString(namespace, value) })); 
    if (!valueValid(value))
      throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9/._-] character in value of Key[%s]", new Object[] { asString(namespace, value) })); 
    this.namespace = Objects.<String>requireNonNull(namespace, "namespace");
    this.value = Objects.<String>requireNonNull(value, "value");
  }
  
  @VisibleForTesting
  static boolean namespaceValid(@NotNull String namespace) {
    for (int i = 0, length = namespace.length(); i < length; i++) {
      if (!NAMESPACE_PREDICATE.test(namespace.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  @VisibleForTesting
  static boolean valueValid(@NotNull String value) {
    for (int i = 0, length = value.length(); i < length; i++) {
      if (!VALUE_PREDICATE.test(value.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  @NotNull
  public String namespace() {
    return this.namespace;
  }
  
  @NotNull
  public String value() {
    return this.value;
  }
  
  @NotNull
  public String asString() {
    return asString(this.namespace, this.value);
  }
  
  @NotNull
  private static String asString(@NotNull String namespace, @NotNull String value) {
    return namespace + ':' + value;
  }
  
  @NotNull
  public String toString() {
    return asString();
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("namespace", this.namespace), 
          ExaminableProperty.of("value", this.value) });
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof Key))
      return false; 
    Key that = (Key)other;
    return (Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value()));
  }
  
  public int hashCode() {
    int result = this.namespace.hashCode();
    result = 31 * result + this.value.hashCode();
    return result;
  }
  
  public int compareTo(@NotNull Key that) {
    return super.compareTo(that);
  }
  
  static int clampCompare(int value) {
    if (value < 0)
      return -1; 
    if (value > 0)
      return 1; 
    return value;
  }
}
