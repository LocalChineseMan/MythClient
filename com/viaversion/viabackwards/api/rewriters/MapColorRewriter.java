package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.rewriter.IdRewriteFunction;

public final class MapColorRewriter {
  public static PacketHandler getRewriteHandler(IdRewriteFunction rewriter) {
    return wrapper -> {
        int iconCount = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
        for (int i = 0; i < iconCount; i++) {
          wrapper.passthrough((Type)Type.VAR_INT);
          wrapper.passthrough((Type)Type.BYTE);
          wrapper.passthrough((Type)Type.BYTE);
          wrapper.passthrough((Type)Type.BYTE);
          if (((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
            wrapper.passthrough(Type.COMPONENT); 
        } 
        short columns = ((Short)wrapper.passthrough((Type)Type.UNSIGNED_BYTE)).shortValue();
        if (columns < 1)
          return; 
        wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
        wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
        wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
        byte[] data = (byte[])wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
        for (int j = 0; j < data.length; j++) {
          int color = data[j] & 0xFF;
          int mappedColor = rewriter.rewrite(color);
          if (mappedColor != -1)
            data[j] = (byte)mappedColor; 
        } 
      };
  }
}
