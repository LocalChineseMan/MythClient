package com.viaversion.viaversion.api.type;

public interface TypeConverter<T> {
  T from(Object paramObject);
}
