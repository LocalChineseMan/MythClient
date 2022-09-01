package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.provider;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;
import de.gerrygames.viarewind.netty.EmptyChannelHandler;
import de.gerrygames.viarewind.netty.ForwardMessageToByteEncoder;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.CompressionSendStorage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class CompressionHandlerProvider implements Provider {
  public void handleSetCompression(UserConnection user, int threshold) {
    ChannelPipeline pipeline = user.getChannel().pipeline();
    if (user.isClientSide()) {
      pipeline.addBefore(Via.getManager().getInjector().getEncoderName(), "compress", getEncoder(threshold));
      pipeline.addBefore(Via.getManager().getInjector().getDecoderName(), "decompress", getDecoder(threshold));
    } else {
      CompressionSendStorage storage = (CompressionSendStorage)user.get(CompressionSendStorage.class);
      storage.setRemoveCompression(true);
    } 
  }
  
  public void handleTransform(UserConnection user) {
    CompressionSendStorage storage = (CompressionSendStorage)user.get(CompressionSendStorage.class);
    if (storage.isRemoveCompression()) {
      ChannelPipeline pipeline = user.getChannel().pipeline();
      String compressor = null;
      String decompressor = null;
      if (pipeline.get("compress") != null) {
        compressor = "compress";
        decompressor = "decompress";
      } else if (pipeline.get("compression-encoder") != null) {
        compressor = "compression-encoder";
        decompressor = "compression-decoder";
      } 
      if (compressor != null) {
        pipeline.replace(decompressor, decompressor, (ChannelHandler)new EmptyChannelHandler());
        pipeline.replace(compressor, compressor, (ChannelHandler)new ForwardMessageToByteEncoder());
      } else {
        throw new IllegalStateException("Couldn't remove compression for 1.7!");
      } 
      storage.setRemoveCompression(false);
    } 
  }
  
  protected ChannelHandler getEncoder(int threshold) {
    return (ChannelHandler)new Compressor(threshold);
  }
  
  protected ChannelHandler getDecoder(int threshold) {
    return (ChannelHandler)new Decompressor(threshold);
  }
  
  private static class CompressionHandlerProvider {}
  
  private static class CompressionHandlerProvider {}
}
