package com.viaversion.viaversion.protocols.protocol1_9to1_8.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.channel.ChannelHandlerContext;

public abstract class MovementTransmitterProvider implements Provider {
  public abstract Object getFlyingPacket();
  
  public abstract Object getGroundPacket();
  
  public void sendPlayer(UserConnection userConnection) {
    ChannelHandlerContext context = PipelineUtil.getContextBefore("decoder", userConnection.getChannel().pipeline());
    if (context != null) {
      if (((MovementTracker)userConnection.get(MovementTracker.class)).isGround()) {
        context.fireChannelRead(getGroundPacket());
      } else {
        context.fireChannelRead(getFlyingPacket());
      } 
      ((MovementTracker)userConnection.get(MovementTracker.class)).incrementIdlePacket();
    } 
  }
}
