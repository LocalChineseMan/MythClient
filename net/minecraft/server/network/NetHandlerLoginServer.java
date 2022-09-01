package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import io.netty.util.concurrent.GenericFutureListener;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
  private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
  
  private static final Logger logger = LogManager.getLogger();
  
  private static final Random RANDOM = new Random();
  
  private final byte[] verifyToken = new byte[4];
  
  private final MinecraftServer server;
  
  public final NetworkManager networkManager;
  
  private LoginState currentLoginState = LoginState.HELLO;
  
  private int connectionTimer;
  
  private GameProfile loginGameProfile;
  
  private final String serverId = "";
  
  private SecretKey secretKey;
  
  private EntityPlayerMP field_181025_l;
  
  public NetHandlerLoginServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_) {
    this.server = p_i45298_1_;
    this.networkManager = p_i45298_2_;
    RANDOM.nextBytes(this.verifyToken);
  }
  
  public void update() {
    if (this.currentLoginState == LoginState.READY_TO_ACCEPT) {
      tryAcceptPlayer();
    } else if (this.currentLoginState == LoginState.DELAY_ACCEPT) {
      EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
      if (entityplayermp == null) {
        this.currentLoginState = LoginState.READY_TO_ACCEPT;
        this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.field_181025_l);
        this.field_181025_l = null;
      } 
    } 
    if (this.connectionTimer++ == 600)
      closeConnection("Took too long to log in"); 
  }
  
  public void closeConnection(String reason) {
    try {
      logger.info("Disconnecting " + getConnectionInfo() + ": " + reason);
      ChatComponentText chatcomponenttext = new ChatComponentText(reason);
      this.networkManager.sendPacket((Packet)new S00PacketDisconnect((IChatComponent)chatcomponenttext));
      this.networkManager.closeChannel((IChatComponent)chatcomponenttext);
    } catch (Exception exception) {
      logger.error("Error whilst disconnecting player", exception);
    } 
  }
  
  public void tryAcceptPlayer() {
    if (!this.loginGameProfile.isComplete())
      this.loginGameProfile = getOfflineProfile(this.loginGameProfile); 
    String s = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);
    if (s != null) {
      closeConnection(s);
    } else {
      this.currentLoginState = LoginState.ACCEPTED;
      if (this.server.getNetworkCompressionTreshold() >= 0 && !this.networkManager.isLocalChannel())
        this.networkManager.sendPacket((Packet)new S03PacketEnableCompression(this.server.getNetworkCompressionTreshold()), (GenericFutureListener)new Object(this), new GenericFutureListener[0]); 
      this.networkManager.sendPacket((Packet)new S02PacketLoginSuccess(this.loginGameProfile));
      EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
      if (entityplayermp != null) {
        this.currentLoginState = LoginState.DELAY_ACCEPT;
        this.field_181025_l = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
      } else {
        this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
      } 
    } 
  }
  
  public void onDisconnect(IChatComponent reason) {
    logger.info(getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
  }
  
  public String getConnectionInfo() {
    return (this.loginGameProfile != null) ? (this.loginGameProfile + " (" + this.networkManager.getRemoteAddress().toString() + ")") : String.valueOf(this.networkManager.getRemoteAddress());
  }
  
  public void processLoginStart(C00PacketLoginStart packetIn) {
    Validate.validState((this.currentLoginState == LoginState.HELLO), "Unexpected hello packet", new Object[0]);
    this.loginGameProfile = packetIn.getProfile();
    if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
      this.currentLoginState = LoginState.KEY;
      getClass();
      this.networkManager.sendPacket((Packet)new S01PacketEncryptionRequest("", this.server.getKeyPair().getPublic(), this.verifyToken));
    } else {
      this.currentLoginState = LoginState.READY_TO_ACCEPT;
    } 
  }
  
  public void processEncryptionResponse(C01PacketEncryptionResponse packetIn) {
    Validate.validState((this.currentLoginState == LoginState.KEY), "Unexpected key packet", new Object[0]);
    PrivateKey privatekey = this.server.getKeyPair().getPrivate();
    if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey)))
      throw new IllegalStateException("Invalid nonce!"); 
    this.secretKey = packetIn.getSecretKey(privatekey);
    this.currentLoginState = LoginState.AUTHENTICATING;
    this.networkManager.enableEncryption(this.secretKey);
    (new Object(this, "User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()))
      
      .start();
  }
  
  protected GameProfile getOfflineProfile(GameProfile original) {
    UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
    return new GameProfile(uuid, original.getName());
  }
  
  enum LoginState {
    HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;
  }
}
