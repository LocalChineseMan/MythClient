package com.viaversion.viaversion.configuration;

import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.util.Config;
import java.io.File;

public abstract class AbstractViaConfig extends Config implements ViaVersionConfig {
  private boolean checkForUpdates;
  
  private boolean preventCollision;
  
  private boolean useNewEffectIndicator;
  
  private boolean useNewDeathmessages;
  
  private boolean suppressMetadataErrors;
  
  private boolean shieldBlocking;
  
  private boolean noDelayShieldBlocking;
  
  private boolean showShieldWhenSwordInHand;
  
  private boolean hologramPatch;
  
  private boolean pistonAnimationPatch;
  
  private boolean bossbarPatch;
  
  private boolean bossbarAntiFlicker;
  
  private double hologramOffset;
  
  private int maxPPS;
  
  private String maxPPSKickMessage;
  
  private int trackingPeriod;
  
  private int warningPPS;
  
  private int maxPPSWarnings;
  
  private String maxPPSWarningsKickMessage;
  
  private boolean sendSupportedVersions;
  
  private boolean simulatePlayerTick;
  
  private boolean itemCache;
  
  private boolean nmsPlayerTicking;
  
  private boolean replacePistons;
  
  private int pistonReplacementId;
  
  private boolean autoTeam;
  
  private boolean forceJsonTransform;
  
  private boolean nbtArrayFix;
  
  private IntSet blockedProtocols;
  
  private String blockedDisconnectMessage;
  
  private String reloadDisconnectMessage;
  
  private boolean suppressConversionWarnings;
  
  private boolean disable1_13TabComplete;
  
  private boolean minimizeCooldown;
  
  private boolean teamColourFix;
  
  private boolean serversideBlockConnections;
  
  private boolean reduceBlockStorageMemory;
  
  private boolean flowerStemWhenBlockAbove;
  
  private boolean vineClimbFix;
  
  private boolean snowCollisionFix;
  
  private boolean infestedBlocksFix;
  
  private int tabCompleteDelay;
  
  private boolean truncate1_14Books;
  
  private boolean leftHandedHandling;
  
  private boolean fullBlockLightFix;
  
  private boolean healthNaNFix;
  
  private boolean instantRespawn;
  
  private boolean ignoreLongChannelNames;
  
  private boolean forcedUse1_17ResourcePack;
  
  private JsonElement resourcePack1_17PromptMessage;
  
  protected AbstractViaConfig(File configFile) {
    super(configFile);
  }
  
  public void reloadConfig() {
    super.reloadConfig();
    loadFields();
  }
  
  protected void loadFields() {
    this.checkForUpdates = getBoolean("checkforupdates", true);
    this.preventCollision = getBoolean("prevent-collision", true);
    this.useNewEffectIndicator = getBoolean("use-new-effect-indicator", true);
    this.useNewDeathmessages = getBoolean("use-new-deathmessages", true);
    this.suppressMetadataErrors = getBoolean("suppress-metadata-errors", false);
    this.shieldBlocking = getBoolean("shield-blocking", true);
    this.noDelayShieldBlocking = getBoolean("no-delay-shield-blocking", false);
    this.showShieldWhenSwordInHand = getBoolean("show-shield-when-sword-in-hand", false);
    this.hologramPatch = getBoolean("hologram-patch", false);
    this.pistonAnimationPatch = getBoolean("piston-animation-patch", false);
    this.bossbarPatch = getBoolean("bossbar-patch", true);
    this.bossbarAntiFlicker = getBoolean("bossbar-anti-flicker", false);
    this.hologramOffset = getDouble("hologram-y", -0.96D);
    this.maxPPS = getInt("max-pps", 800);
    this.maxPPSKickMessage = getString("max-pps-kick-msg", "Sending packets too fast? lag?");
    this.trackingPeriod = getInt("tracking-period", 6);
    this.warningPPS = getInt("tracking-warning-pps", 120);
    this.maxPPSWarnings = getInt("tracking-max-warnings", 3);
    this.maxPPSWarningsKickMessage = getString("tracking-max-kick-msg", "You are sending too many packets, :(");
    this.sendSupportedVersions = getBoolean("send-supported-versions", false);
    this.simulatePlayerTick = getBoolean("simulate-pt", true);
    this.itemCache = getBoolean("item-cache", true);
    this.nmsPlayerTicking = getBoolean("nms-player-ticking", true);
    this.replacePistons = getBoolean("replace-pistons", false);
    this.pistonReplacementId = getInt("replacement-piston-id", 0);
    this.autoTeam = getBoolean("auto-team", true);
    this.forceJsonTransform = getBoolean("force-json-transform", false);
    this.nbtArrayFix = getBoolean("chat-nbt-fix", true);
    this.blockedProtocols = (IntSet)new IntOpenHashSet(getIntegerList("block-protocols"));
    this.blockedDisconnectMessage = getString("block-disconnect-msg", "You are using an unsupported Minecraft version!");
    this.reloadDisconnectMessage = getString("reload-disconnect-msg", "Server reload, please rejoin!");
    this.minimizeCooldown = getBoolean("minimize-cooldown", true);
    this.teamColourFix = getBoolean("team-colour-fix", true);
    this.suppressConversionWarnings = getBoolean("suppress-conversion-warnings", false);
    this.disable1_13TabComplete = getBoolean("disable-1_13-auto-complete", false);
    this.serversideBlockConnections = getBoolean("serverside-blockconnections", true);
    this.reduceBlockStorageMemory = getBoolean("reduce-blockstorage-memory", false);
    this.flowerStemWhenBlockAbove = getBoolean("flowerstem-when-block-above", false);
    this.vineClimbFix = getBoolean("vine-climb-fix", false);
    this.snowCollisionFix = getBoolean("fix-low-snow-collision", false);
    this.infestedBlocksFix = getBoolean("fix-infested-block-breaking", true);
    this.tabCompleteDelay = getInt("1_13-tab-complete-delay", 0);
    this.truncate1_14Books = getBoolean("truncate-1_14-books", false);
    this.leftHandedHandling = getBoolean("left-handed-handling", true);
    this.fullBlockLightFix = getBoolean("fix-non-full-blocklight", false);
    this.healthNaNFix = getBoolean("fix-1_14-health-nan", true);
    this.instantRespawn = getBoolean("use-1_15-instant-respawn", false);
    this.ignoreLongChannelNames = getBoolean("ignore-long-1_16-channel-names", true);
    this.forcedUse1_17ResourcePack = getBoolean("forced-use-1_17-resource-pack", false);
    this.resourcePack1_17PromptMessage = getSerializedComponent("resource-pack-1_17-prompt");
  }
  
  public boolean isCheckForUpdates() {
    return this.checkForUpdates;
  }
  
  public void setCheckForUpdates(boolean checkForUpdates) {
    this.checkForUpdates = checkForUpdates;
    set("checkforupdates", Boolean.valueOf(checkForUpdates));
  }
  
  public boolean isPreventCollision() {
    return this.preventCollision;
  }
  
  public boolean isNewEffectIndicator() {
    return this.useNewEffectIndicator;
  }
  
  public boolean isShowNewDeathMessages() {
    return this.useNewDeathmessages;
  }
  
  public boolean isSuppressMetadataErrors() {
    return this.suppressMetadataErrors;
  }
  
  public boolean isShieldBlocking() {
    return this.shieldBlocking;
  }
  
  public boolean isNoDelayShieldBlocking() {
    return this.noDelayShieldBlocking;
  }
  
  public boolean isShowShieldWhenSwordInHand() {
    return this.showShieldWhenSwordInHand;
  }
  
  public boolean isHologramPatch() {
    return this.hologramPatch;
  }
  
  public boolean isPistonAnimationPatch() {
    return this.pistonAnimationPatch;
  }
  
  public boolean isBossbarPatch() {
    return this.bossbarPatch;
  }
  
  public boolean isBossbarAntiflicker() {
    return this.bossbarAntiFlicker;
  }
  
  public double getHologramYOffset() {
    return this.hologramOffset;
  }
  
  public int getMaxPPS() {
    return this.maxPPS;
  }
  
  public String getMaxPPSKickMessage() {
    return this.maxPPSKickMessage;
  }
  
  public int getTrackingPeriod() {
    return this.trackingPeriod;
  }
  
  public int getWarningPPS() {
    return this.warningPPS;
  }
  
  public int getMaxWarnings() {
    return this.maxPPSWarnings;
  }
  
  public String getMaxWarningsKickMessage() {
    return this.maxPPSWarningsKickMessage;
  }
  
  public boolean isAntiXRay() {
    return false;
  }
  
  public boolean isSendSupportedVersions() {
    return this.sendSupportedVersions;
  }
  
  public boolean isSimulatePlayerTick() {
    return this.simulatePlayerTick;
  }
  
  public boolean isItemCache() {
    return this.itemCache;
  }
  
  public boolean isNMSPlayerTicking() {
    return this.nmsPlayerTicking;
  }
  
  public boolean isReplacePistons() {
    return this.replacePistons;
  }
  
  public int getPistonReplacementId() {
    return this.pistonReplacementId;
  }
  
  public boolean isAutoTeam() {
    return (this.preventCollision && this.autoTeam);
  }
  
  public boolean isForceJsonTransform() {
    return this.forceJsonTransform;
  }
  
  public boolean is1_12NBTArrayFix() {
    return this.nbtArrayFix;
  }
  
  public boolean is1_12QuickMoveActionFix() {
    return false;
  }
  
  public IntSet getBlockedProtocols() {
    return this.blockedProtocols;
  }
  
  public String getBlockedDisconnectMsg() {
    return this.blockedDisconnectMessage;
  }
  
  public String getReloadDisconnectMsg() {
    return this.reloadDisconnectMessage;
  }
  
  public boolean isMinimizeCooldown() {
    return this.minimizeCooldown;
  }
  
  public boolean is1_13TeamColourFix() {
    return this.teamColourFix;
  }
  
  public boolean isSuppressConversionWarnings() {
    return this.suppressConversionWarnings;
  }
  
  public boolean isDisable1_13AutoComplete() {
    return this.disable1_13TabComplete;
  }
  
  public boolean isServersideBlockConnections() {
    return this.serversideBlockConnections;
  }
  
  public String getBlockConnectionMethod() {
    return "packet";
  }
  
  public boolean isReduceBlockStorageMemory() {
    return this.reduceBlockStorageMemory;
  }
  
  public boolean isStemWhenBlockAbove() {
    return this.flowerStemWhenBlockAbove;
  }
  
  public boolean isVineClimbFix() {
    return this.vineClimbFix;
  }
  
  public boolean isSnowCollisionFix() {
    return this.snowCollisionFix;
  }
  
  public boolean isInfestedBlocksFix() {
    return this.infestedBlocksFix;
  }
  
  public int get1_13TabCompleteDelay() {
    return this.tabCompleteDelay;
  }
  
  public boolean isTruncate1_14Books() {
    return this.truncate1_14Books;
  }
  
  public boolean isLeftHandedHandling() {
    return this.leftHandedHandling;
  }
  
  public boolean is1_9HitboxFix() {
    return false;
  }
  
  public boolean is1_14HitboxFix() {
    return false;
  }
  
  public boolean isNonFullBlockLightFix() {
    return this.fullBlockLightFix;
  }
  
  public boolean is1_14HealthNaNFix() {
    return this.healthNaNFix;
  }
  
  public boolean is1_15InstantRespawn() {
    return this.instantRespawn;
  }
  
  public boolean isIgnoreLong1_16ChannelNames() {
    return this.ignoreLongChannelNames;
  }
  
  public boolean isForcedUse1_17ResourcePack() {
    return this.forcedUse1_17ResourcePack;
  }
  
  public JsonElement get1_17ResourcePackPrompt() {
    return this.resourcePack1_17PromptMessage;
  }
}
