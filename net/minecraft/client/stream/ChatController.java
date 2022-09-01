package net.minecraft.client.stream;

import java.util.HashMap;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.CoreAPI;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatAPI;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController {
  private static final Logger LOGGER = LogManager.getLogger();
  
  protected ChatListener field_153003_a = null;
  
  protected String field_153004_b = "";
  
  protected String field_153006_d = "";
  
  protected String field_153007_e = "";
  
  protected Core field_175992_e = null;
  
  protected Chat field_153008_f = null;
  
  protected ChatState field_153011_i = ChatState.Uninitialized;
  
  protected AuthToken field_153012_j = new AuthToken();
  
  protected HashMap<String, ChatChannelListener> field_175998_i = new HashMap<>();
  
  protected int field_153015_m = 128;
  
  protected EnumEmoticonMode field_175997_k = EnumEmoticonMode.None;
  
  protected EnumEmoticonMode field_175995_l = EnumEmoticonMode.None;
  
  protected ChatEmoticonData field_175996_m = null;
  
  protected int field_175993_n = 500;
  
  protected int field_175994_o = 2000;
  
  protected IChatAPIListener field_175999_p = new IChatAPIListener() {
      public void chatInitializationCallback(ErrorCode p_chatInitializationCallback_1_) {
        if (ErrorCode.succeeded(p_chatInitializationCallback_1_)) {
          ChatController.this.field_153008_f.setMessageFlushInterval(ChatController.this.field_175993_n);
          ChatController.this.field_153008_f.setUserChangeEventInterval(ChatController.this.field_175994_o);
          ChatController.this.func_153001_r();
          ChatController.this.func_175985_a(ChatController.ChatState.Initialized);
        } else {
          ChatController.this.func_175985_a(ChatController.ChatState.Uninitialized);
        } 
        try {
          if (ChatController.this.field_153003_a != null)
            ChatController.this.field_153003_a.func_176023_d(p_chatInitializationCallback_1_); 
        } catch (Exception exception) {
          ChatController.this.func_152995_h(exception.toString());
        } 
      }
      
      public void chatShutdownCallback(ErrorCode p_chatShutdownCallback_1_) {
        if (ErrorCode.succeeded(p_chatShutdownCallback_1_)) {
          ErrorCode errorcode = ChatController.this.field_175992_e.shutdown();
          if (ErrorCode.failed(errorcode)) {
            String s = ErrorCode.getString(errorcode);
            ChatController.this.func_152995_h(String.format("Error shutting down the Twitch sdk: %s", new Object[] { s }));
          } 
          ChatController.this.func_175985_a(ChatController.ChatState.Uninitialized);
        } else {
          ChatController.this.func_175985_a(ChatController.ChatState.Initialized);
          ChatController.this.func_152995_h(String.format("Error shutting down Twith chat: %s", new Object[] { p_chatShutdownCallback_1_ }));
        } 
        try {
          if (ChatController.this.field_153003_a != null)
            ChatController.this.field_153003_a.func_176022_e(p_chatShutdownCallback_1_); 
        } catch (Exception exception) {
          ChatController.this.func_152995_h(exception.toString());
        } 
      }
      
      public void chatEmoticonDataDownloadCallback(ErrorCode p_chatEmoticonDataDownloadCallback_1_) {
        if (ErrorCode.succeeded(p_chatEmoticonDataDownloadCallback_1_))
          ChatController.this.func_152988_s(); 
      }
    };
  
  public void func_152990_a(ChatListener p_152990_1_) {
    this.field_153003_a = p_152990_1_;
  }
  
  public void func_152994_a(AuthToken p_152994_1_) {
    this.field_153012_j = p_152994_1_;
  }
  
  public void func_152984_a(String p_152984_1_) {
    this.field_153006_d = p_152984_1_;
  }
  
  public void func_152998_c(String p_152998_1_) {
    this.field_153004_b = p_152998_1_;
  }
  
  public ChatState func_153000_j() {
    return this.field_153011_i;
  }
  
  public boolean func_175990_d(String p_175990_1_) {
    if (!this.field_175998_i.containsKey(p_175990_1_))
      return false; 
    ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175990_1_);
    return (chatcontroller$chatchannellistener.func_176040_a() == EnumChannelState.Connected);
  }
  
  public EnumChannelState func_175989_e(String p_175989_1_) {
    if (!this.field_175998_i.containsKey(p_175989_1_))
      return EnumChannelState.Disconnected; 
    ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175989_1_);
    return chatcontroller$chatchannellistener.func_176040_a();
  }
  
  public ChatController() {
    this.field_175992_e = Core.getInstance();
    if (this.field_175992_e == null)
      this.field_175992_e = new Core((CoreAPI)new StandardCoreAPI()); 
    this.field_153008_f = new Chat((ChatAPI)new StandardChatAPI());
  }
  
  public boolean func_175984_n() {
    if (this.field_153011_i != ChatState.Uninitialized)
      return false; 
    func_175985_a(ChatState.Initializing);
    ErrorCode errorcode = this.field_175992_e.initialize(this.field_153006_d, null);
    if (ErrorCode.failed(errorcode)) {
      func_175985_a(ChatState.Uninitialized);
      String s1 = ErrorCode.getString(errorcode);
      func_152995_h(String.format("Error initializing Twitch sdk: %s", new Object[] { s1 }));
      return false;
    } 
    this.field_175995_l = this.field_175997_k;
    HashSet<ChatTokenizationOption> hashset = new HashSet<>();
    switch (null.$SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode[this.field_175997_k.ordinal()]) {
      case 1:
        hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
        break;
      case 2:
        hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
        break;
      case 3:
        hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
        break;
    } 
    errorcode = this.field_153008_f.initialize(hashset, this.field_175999_p);
    if (ErrorCode.failed(errorcode)) {
      this.field_175992_e.shutdown();
      func_175985_a(ChatState.Uninitialized);
      String s = ErrorCode.getString(errorcode);
      func_152995_h(String.format("Error initializing Twitch chat: %s", new Object[] { s }));
      return false;
    } 
    func_175985_a(ChatState.Initialized);
    return true;
  }
  
  public boolean func_152986_d(String p_152986_1_) {
    return func_175987_a(p_152986_1_, false);
  }
  
  protected boolean func_175987_a(String p_175987_1_, boolean p_175987_2_) {
    if (this.field_153011_i != ChatState.Initialized)
      return false; 
    if (this.field_175998_i.containsKey(p_175987_1_)) {
      func_152995_h("Already in channel: " + p_175987_1_);
      return false;
    } 
    if (p_175987_1_ != null && !p_175987_1_.equals("")) {
      ChatChannelListener chatcontroller$chatchannellistener = new ChatChannelListener(this, p_175987_1_);
      this.field_175998_i.put(p_175987_1_, chatcontroller$chatchannellistener);
      boolean flag = chatcontroller$chatchannellistener.func_176038_a(p_175987_2_);
      if (!flag)
        this.field_175998_i.remove(p_175987_1_); 
      return flag;
    } 
    return false;
  }
  
  public boolean func_175991_l(String p_175991_1_) {
    if (this.field_153011_i != ChatState.Initialized)
      return false; 
    if (!this.field_175998_i.containsKey(p_175991_1_)) {
      func_152995_h("Not in channel: " + p_175991_1_);
      return false;
    } 
    ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175991_1_);
    return chatcontroller$chatchannellistener.func_176034_g();
  }
  
  public boolean func_152993_m() {
    if (this.field_153011_i != ChatState.Initialized)
      return false; 
    ErrorCode errorcode = this.field_153008_f.shutdown();
    if (ErrorCode.failed(errorcode)) {
      String s = ErrorCode.getString(errorcode);
      func_152995_h(String.format("Error shutting down chat: %s", new Object[] { s }));
      return false;
    } 
    func_152996_t();
    func_175985_a(ChatState.ShuttingDown);
    return true;
  }
  
  public void func_175988_p() {
    if (func_153000_j() != ChatState.Uninitialized) {
      func_152993_m();
      if (func_153000_j() == ChatState.ShuttingDown)
        while (func_153000_j() != ChatState.Uninitialized) {
          try {
            Thread.sleep(200L);
            func_152997_n();
          } catch (InterruptedException interruptedException) {}
        }  
    } 
  }
  
  public void func_152997_n() {
    if (this.field_153011_i != ChatState.Uninitialized) {
      ErrorCode errorcode = this.field_153008_f.flushEvents();
      if (ErrorCode.failed(errorcode)) {
        String s = ErrorCode.getString(errorcode);
        func_152995_h(String.format("Error flushing chat events: %s", new Object[] { s }));
      } 
    } 
  }
  
  public boolean func_175986_a(String p_175986_1_, String p_175986_2_) {
    if (this.field_153011_i != ChatState.Initialized)
      return false; 
    if (!this.field_175998_i.containsKey(p_175986_1_)) {
      func_152995_h("Not in channel: " + p_175986_1_);
      return false;
    } 
    ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175986_1_);
    return chatcontroller$chatchannellistener.func_176037_b(p_175986_2_);
  }
  
  protected void func_175985_a(ChatState p_175985_1_) {
    if (p_175985_1_ != this.field_153011_i) {
      this.field_153011_i = p_175985_1_;
      try {
        if (this.field_153003_a != null)
          this.field_153003_a.func_176017_a(p_175985_1_); 
      } catch (Exception exception) {
        func_152995_h(exception.toString());
      } 
    } 
  }
  
  protected void func_153001_r() {
    if (this.field_175995_l != EnumEmoticonMode.None)
      if (this.field_175996_m == null) {
        ErrorCode errorcode = this.field_153008_f.downloadEmoticonData();
        if (ErrorCode.failed(errorcode)) {
          String s = ErrorCode.getString(errorcode);
          func_152995_h(String.format("Error trying to download emoticon data: %s", new Object[] { s }));
        } 
      }  
  }
  
  protected void func_152988_s() {
    if (this.field_175996_m == null) {
      this.field_175996_m = new ChatEmoticonData();
      ErrorCode errorcode = this.field_153008_f.getEmoticonData(this.field_175996_m);
      if (ErrorCode.succeeded(errorcode)) {
        try {
          if (this.field_153003_a != null)
            this.field_153003_a.func_176021_d(); 
        } catch (Exception exception) {
          func_152995_h(exception.toString());
        } 
      } else {
        func_152995_h("Error preparing emoticon data: " + ErrorCode.getString(errorcode));
      } 
    } 
  }
  
  protected void func_152996_t() {
    if (this.field_175996_m != null) {
      ErrorCode errorcode = this.field_153008_f.clearEmoticonData();
      if (ErrorCode.succeeded(errorcode)) {
        this.field_175996_m = null;
        try {
          if (this.field_153003_a != null)
            this.field_153003_a.func_176024_e(); 
        } catch (Exception exception) {
          func_152995_h(exception.toString());
        } 
      } else {
        func_152995_h("Error clearing emoticon data: " + ErrorCode.getString(errorcode));
      } 
    } 
  }
  
  protected void func_152995_h(String p_152995_1_) {
    LOGGER.error(TwitchStream.STREAM_MARKER, "[Chat controller] {}", new Object[] { p_152995_1_ });
  }
  
  public class ChatController {}
  
  public static interface ChatListener {
    void func_176023_d(ErrorCode param1ErrorCode);
    
    void func_176022_e(ErrorCode param1ErrorCode);
    
    void func_176021_d();
    
    void func_176024_e();
    
    void func_176017_a(ChatController.ChatState param1ChatState);
    
    void func_176025_a(String param1String, ChatTokenizedMessage[] param1ArrayOfChatTokenizedMessage);
    
    void func_180605_a(String param1String, ChatRawMessage[] param1ArrayOfChatRawMessage);
    
    void func_176018_a(String param1String, ChatUserInfo[] param1ArrayOfChatUserInfo1, ChatUserInfo[] param1ArrayOfChatUserInfo2, ChatUserInfo[] param1ArrayOfChatUserInfo3);
    
    void func_180606_a(String param1String);
    
    void func_180607_b(String param1String);
    
    void func_176019_a(String param1String1, String param1String2);
    
    void func_176016_c(String param1String);
    
    void func_176020_d(String param1String);
  }
  
  public enum ChatState {
    Uninitialized, Initializing, Initialized, ShuttingDown;
  }
  
  public enum ChatController {
  
  }
  
  public enum EnumEmoticonMode {
    None, Url, TextureAtlas;
  }
}
