package notthatuwu.xyz.mythrecode.api.viamcp.utils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class NettyUtil {
  public static ChannelPipeline decodeEncodePlacement(ChannelPipeline instance, String base, String newHandler, ChannelHandler handler) {
    switch (base) {
      case "decoder":
        if (instance.get("via-decoder") != null)
          base = "via-decoder"; 
        break;
      case "encoder":
        if (instance.get("via-encoder") != null)
          base = "via-encoder"; 
        break;
    } 
    return instance.addBefore(base, newHandler, handler);
  }
}
