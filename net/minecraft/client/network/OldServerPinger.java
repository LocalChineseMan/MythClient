package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
  private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on(false).limit(6);
  
  private static final Logger logger = LogManager.getLogger();
  
  private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());
  
  private final boolean keepPing;
  
  public OldServerPinger() {
    this(false);
  }
  
  public OldServerPinger(boolean keepPing) {
    this.keepPing = keepPing;
  }
  
  public void ping(final ServerData server) throws UnknownHostException {
    ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
    final NetworkManager networkmanager = NetworkManager.func_181124_a(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
    this.pingDestinations.add(networkmanager);
    server.serverMOTD = "Pinging...";
    if (!this.keepPing)
      server.pingToServer = -1L; 
    server.playerList = null;
    networkmanager.setNetHandler((INetHandler)new INetHandlerStatusClient() {
          private boolean field_147403_d = false;
          
          private boolean field_183009_e = false;
          
          private long field_175092_e = 0L;
          
          public void handleServerInfo(S00PacketServerInfo packetIn) {
            if (this.field_183009_e) {
              networkmanager.closeChannel((IChatComponent)new ChatComponentText("Received unrequested status"));
            } else {
              this.field_183009_e = true;
              ServerStatusResponse serverstatusresponse = packetIn.getResponse();
              if (serverstatusresponse.getServerDescription() != null) {
                server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
              } else {
                server.serverMOTD = "";
              } 
              if (serverstatusresponse.getProtocolVersionInfo() != null) {
                server.gameVersion = serverstatusresponse.getProtocolVersionInfo().getName();
                server.version = serverstatusresponse.getProtocolVersionInfo().getProtocol();
              } else {
                server.gameVersion = "Old";
                server.version = 0;
              } 
              if (serverstatusresponse.getPlayerCountData() != null) {
                server.populationInfo = EnumChatFormatting.GRAY + "" + serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverstatusresponse.getPlayerCountData().getMaxPlayers();
                if (ArrayUtils.isNotEmpty((Object[])serverstatusresponse.getPlayerCountData().getPlayers())) {
                  StringBuilder stringbuilder = new StringBuilder();
                  for (GameProfile gameprofile : serverstatusresponse.getPlayerCountData().getPlayers()) {
                    if (stringbuilder.length() > 0)
                      stringbuilder.append("\n"); 
                    stringbuilder.append(gameprofile.getName());
                  } 
                  if ((serverstatusresponse.getPlayerCountData().getPlayers()).length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
                    if (stringbuilder.length() > 0)
                      stringbuilder.append("\n"); 
                    stringbuilder.append("... and ").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - (serverstatusresponse.getPlayerCountData().getPlayers()).length).append(" more ...");
                  } 
                  server.playerList = stringbuilder.toString();
                } 
              } else {
                server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
              } 
              if (serverstatusresponse.getFavicon() != null) {
                String s = serverstatusresponse.getFavicon();
                if (s.startsWith("data:image/png;base64,")) {
                  server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                } else {
                  OldServerPinger.logger.error("Invalid server icon (unknown format)");
                } 
              } else {
                server.setBase64EncodedIconData(null);
              } 
              this.field_175092_e = Minecraft.getSystemTime();
              networkmanager.sendPacket((Packet)new C01PacketPing(this.field_175092_e));
              this.field_147403_d = true;
            } 
          }
          
          public void handlePong(S01PacketPong packetIn) {
            long i = this.field_175092_e;
            long j = Minecraft.getSystemTime();
            server.pingToServer = j - i;
            networkmanager.closeChannel((IChatComponent)new ChatComponentText("Finished"));
          }
          
          public void onDisconnect(IChatComponent reason) {
            if (!this.field_147403_d) {
              OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
              server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
              server.populationInfo = "";
              OldServerPinger.this.tryCompatibilityPing(server);
            } 
          }
        });
    try {
      networkmanager.sendPacket((Packet)new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
      networkmanager.sendPacket((Packet)new C00PacketServerQuery());
    } catch (Throwable throwable) {
      logger.error(throwable);
    } 
  }
  
  private void tryCompatibilityPing(ServerData server) {
    ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
    ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())).handler((ChannelHandler)new Object(this, serveraddress, server)))
      
      .channel(NioSocketChannel.class)).connect(serveraddress.getIP(), serveraddress.getPort());
  }
  
  public void pingPendingNetworks() {
    synchronized (this.pingDestinations) {
      Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
      while (iterator.hasNext()) {
        NetworkManager networkmanager = iterator.next();
        if (networkmanager.isChannelOpen()) {
          networkmanager.processReceivedPackets();
          continue;
        } 
        iterator.remove();
        networkmanager.checkDisconnected();
      } 
    } 
  }
  
  public void clearPendingNetworks() {
    synchronized (this.pingDestinations) {
      Iterator<NetworkManager> iterator = this.pingDestinations.iterator();
      while (iterator.hasNext()) {
        NetworkManager networkmanager = iterator.next();
        if (networkmanager.isChannelOpen()) {
          iterator.remove();
          networkmanager.closeChannel((IChatComponent)new ChatComponentText("Cancelled"));
        } 
      } 
    } 
  }
}
