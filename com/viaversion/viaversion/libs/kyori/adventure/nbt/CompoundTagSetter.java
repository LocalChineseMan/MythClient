package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CompoundTagSetter<R> {
  @NotNull
  R put(@NotNull String paramString, @NotNull BinaryTag paramBinaryTag);
  
  @NotNull
  R put(@NotNull CompoundBinaryTag paramCompoundBinaryTag);
  
  @NotNull
  R put(@NotNull Map<String, ? extends BinaryTag> paramMap);
  
  @NotNull
  default R remove(@NotNull String key) {
    return remove(key, null);
  }
  
  @NotNull
  R remove(@NotNull String paramString, @Nullable Consumer<? super BinaryTag> paramConsumer);
  
  @NotNull
  default R putBoolean(@NotNull String key, boolean value) {
    return put(key, value ? (BinaryTag)ByteBinaryTag.ONE : (BinaryTag)ByteBinaryTag.ZERO);
  }
  
  @NotNull
  default R putByte(@NotNull String key, byte value) {
    return put(key, (BinaryTag)ByteBinaryTag.of(value));
  }
  
  @NotNull
  default R putShort(@NotNull String key, short value) {
    return put(key, (BinaryTag)ShortBinaryTag.of(value));
  }
  
  @NotNull
  default R putInt(@NotNull String key, int value) {
    return put(key, (BinaryTag)IntBinaryTag.of(value));
  }
  
  @NotNull
  default R putLong(@NotNull String key, long value) {
    return put(key, (BinaryTag)LongBinaryTag.of(value));
  }
  
  @NotNull
  default R putFloat(@NotNull String key, float value) {
    return put(key, (BinaryTag)FloatBinaryTag.of(value));
  }
  
  @NotNull
  default R putDouble(@NotNull String key, double value) {
    return put(key, (BinaryTag)DoubleBinaryTag.of(value));
  }
  
  @NotNull
  default R putByteArray(@NotNull String key, byte[] value) {
    return put(key, (BinaryTag)ByteArrayBinaryTag.of(value));
  }
  
  @NotNull
  default R putString(@NotNull String key, @NotNull String value) {
    return put(key, (BinaryTag)StringBinaryTag.of(value));
  }
  
  @NotNull
  default R putIntArray(@NotNull String key, int[] value) {
    return put(key, (BinaryTag)IntArrayBinaryTag.of(value));
  }
  
  @NotNull
  default R putLongArray(@NotNull String key, long[] value) {
    return put(key, (BinaryTag)LongArrayBinaryTag.of(value));
  }
}
