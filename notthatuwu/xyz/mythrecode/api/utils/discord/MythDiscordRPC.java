package notthatuwu.xyz.mythrecode.api.utils.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import org.apache.commons.lang3.RandomUtils;

public class MythDiscordRPC {
  private boolean running = true;
  
  private long created = 0L;
  
  public String user;
  
  public String tag;
  
  public String format;
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public String getUser() {
    return this.user;
  }
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public void setFormat(String format) {
    this.format = format;
  }
  
  public String getFormat() {
    return this.format;
  }
  
  public String[] rpcLogoList = new String[] { "mythlogomos1", "mythlogomos2", "mythlogomos3", "mythlogospoof1" };
  
  public void start() {
    this.created = System.currentTimeMillis();
    DiscordEventHandlers handlers = (new DiscordEventHandlers.Builder()).setReadyEventHandler(new ReadyCallback() {
          public void apply(DiscordUser user) {
            System.out.println("Hello " + user.username + "#" + user.discriminator + ".");
            MythDiscordRPC.this.setUser(user.username);
            MythDiscordRPC.this.setTag(user.discriminator);
            MythDiscordRPC.this.setFormat(user.username + "#" + user.discriminator);
          }
        }).build();
    DiscordRPC.discordInitialize("895656708934869044", handlers, true);
    (new Thread("Discord RPC Callback") {
        public void run() {
          while (MythDiscordRPC.this.running)
            DiscordRPC.discordRunCallbacks(); 
        }
      }).start();
  }
  
  public void shutdown() {
    this.running = false;
    DiscordRPC.discordShutdown();
  }
  
  public void update(String firstline, String secondline) {
    DiscordRichPresence a = new DiscordRichPresence();
    DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
    b.setBigImage(this.rpcLogoList[RandomUtils.nextInt(0, this.rpcLogoList.length)], "Join our discord server! (discord.gg/zc9gsSHrtZ)");
    b.setDetails(firstline);
    b.setStartTimestamps(this.created);
    DiscordRPC.discordUpdatePresence(b.build());
  }
  
  public void update(String firstline, String secondline, String smallImageKey, String smallImageText) {
    DiscordRichPresence a = new DiscordRichPresence();
    DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
    b.setBigImage("newlogo", "Myth Client haram");
    b.setSmallImage(smallImageKey, smallImageText);
    b.setDetails(firstline);
    b.setStartTimestamps(this.created);
    DiscordRPC.discordUpdatePresence(b.build());
  }
}
