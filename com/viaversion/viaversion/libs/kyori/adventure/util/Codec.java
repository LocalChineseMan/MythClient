package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;

public interface Codec<D, E, DX extends Throwable, EX extends Throwable> {
  @NotNull
  static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> of(@NotNull final Decoder<D, E, DX> decoder, @NotNull final Encoder<D, E, EX> encoder) {
    return new Codec<D, E, DX, EX>() {
        @NotNull
        public D decode(@NotNull E encoded) throws DX {
          return (D)decoder.decode(encoded);
        }
        
        @NotNull
        public E encode(@NotNull D decoded) throws EX {
          return (E)encoder.encode(decoded);
        }
      };
  }
  
  @NotNull
  D decode(@NotNull E paramE) throws DX;
  
  @NotNull
  E encode(@NotNull D paramD) throws EX;
  
  public static interface Decoder<D, E, X extends Throwable> {
    @NotNull
    D decode(@NotNull E param1E) throws X;
  }
  
  public static interface Encoder<D, E, X extends Throwable> {
    @NotNull
    E encode(@NotNull D param1D) throws X;
  }
}
