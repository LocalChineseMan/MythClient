package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.util.ServiceLoader;

final class Services0 {
  static <S> ServiceLoader<S> loader(Class<S> type) {
    return ServiceLoader.load(type, type.getClassLoader());
  }
}
