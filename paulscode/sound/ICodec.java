package paulscode.sound;

import java.net.URL;
import javax.sound.sampled.AudioFormat;

public interface ICodec {
  void reverseByteOrder(boolean paramBoolean);
  
  boolean initialize(URL paramURL);
  
  boolean initialized();
  
  SoundBuffer read();
  
  SoundBuffer readAll();
  
  boolean endOfStream();
  
  void cleanup();
  
  AudioFormat getAudioFormat();
}
