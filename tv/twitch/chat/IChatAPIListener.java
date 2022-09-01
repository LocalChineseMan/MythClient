package tv.twitch.chat;

import tv.twitch.ErrorCode;

public interface IChatAPIListener {
  void chatInitializationCallback(ErrorCode paramErrorCode);
  
  void chatShutdownCallback(ErrorCode paramErrorCode);
  
  void chatEmoticonDataDownloadCallback(ErrorCode paramErrorCode);
}
