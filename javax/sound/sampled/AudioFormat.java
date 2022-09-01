package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFormat {
  protected Encoding encoding;
  
  protected float sampleRate;
  
  protected int sampleSizeInBits;
  
  protected int channels;
  
  protected int frameSize;
  
  protected float frameRate;
  
  protected boolean bigEndian;
  
  private HashMap<String, Object> properties;
  
  public AudioFormat(Encoding paramEncoding, float paramFloat1, int paramInt1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean) {
    this.encoding = paramEncoding;
    this.sampleRate = paramFloat1;
    this.sampleSizeInBits = paramInt1;
    this.channels = paramInt2;
    this.frameSize = paramInt3;
    this.frameRate = paramFloat2;
    this.bigEndian = paramBoolean;
    this.properties = null;
  }
  
  public AudioFormat(Encoding paramEncoding, float paramFloat1, int paramInt1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean, Map<String, Object> paramMap) {
    this(paramEncoding, paramFloat1, paramInt1, paramInt2, paramInt3, paramFloat2, paramBoolean);
    this.properties = new HashMap<>(paramMap);
  }
  
  public AudioFormat(float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    this((paramBoolean1 == true) ? Encoding.PCM_SIGNED : Encoding.PCM_UNSIGNED, paramFloat, paramInt1, paramInt2, (paramInt2 == -1 || paramInt1 == -1) ? -1 : ((paramInt1 + 7) / 8 * paramInt2), paramFloat, paramBoolean2);
  }
  
  public Encoding getEncoding() {
    return this.encoding;
  }
  
  public float getSampleRate() {
    return this.sampleRate;
  }
  
  public int getSampleSizeInBits() {
    return this.sampleSizeInBits;
  }
  
  public int getChannels() {
    return this.channels;
  }
  
  public int getFrameSize() {
    return this.frameSize;
  }
  
  public float getFrameRate() {
    return this.frameRate;
  }
  
  public boolean isBigEndian() {
    return this.bigEndian;
  }
  
  public Map<String, Object> properties() {
    Map<? extends String, ?> map;
    if (this.properties == null) {
      map = (Map)new HashMap<>(0);
    } else {
      map = (Map)this.properties.clone();
    } 
    return Collections.unmodifiableMap(map);
  }
  
  public Object getProperty(String paramString) {
    if (this.properties == null)
      return null; 
    return this.properties.get(paramString);
  }
  
  public boolean matches(AudioFormat paramAudioFormat) {
    if (paramAudioFormat.getEncoding().equals(getEncoding()) && (paramAudioFormat
      .getChannels() == -1 || paramAudioFormat
      .getChannels() == getChannels()) && (paramAudioFormat
      .getSampleRate() == -1.0F || paramAudioFormat
      .getSampleRate() == getSampleRate()) && (paramAudioFormat
      .getSampleSizeInBits() == -1 || paramAudioFormat
      .getSampleSizeInBits() == getSampleSizeInBits()) && (paramAudioFormat
      .getFrameRate() == -1.0F || paramAudioFormat
      .getFrameRate() == getFrameRate()) && (paramAudioFormat
      .getFrameSize() == -1 || paramAudioFormat
      .getFrameSize() == getFrameSize()) && (
      getSampleSizeInBits() <= 8 || paramAudioFormat
      .isBigEndian() == isBigEndian()))
      return true; 
    return false;
  }
  
  public String toString() {
    String str2, str3, str4, str5, str1 = "";
    if (getEncoding() != null)
      str1 = getEncoding().toString() + " "; 
    if (getSampleRate() == -1.0F) {
      str2 = "unknown sample rate, ";
    } else {
      str2 = "" + getSampleRate() + " Hz, ";
    } 
    if (getSampleSizeInBits() == -1.0F) {
      str3 = "unknown bits per sample, ";
    } else {
      str3 = "" + getSampleSizeInBits() + " bit, ";
    } 
    if (getChannels() == 1) {
      str4 = "mono, ";
    } else if (getChannels() == 2) {
      str4 = "stereo, ";
    } else if (getChannels() == -1) {
      str4 = " unknown number of channels, ";
    } else {
      str4 = "" + getChannels() + " channels, ";
    } 
    if (getFrameSize() == -1.0F) {
      str5 = "unknown frame size, ";
    } else {
      str5 = "" + getFrameSize() + " bytes/frame, ";
    } 
    String str6 = "";
    if (Math.abs(getSampleRate() - getFrameRate()) > 1.0E-5D)
      if (getFrameRate() == -1.0F) {
        str6 = "unknown frame rate, ";
      } else {
        str6 = getFrameRate() + " frames/second, ";
      }  
    String str7 = "";
    if ((getEncoding().equals(Encoding.PCM_SIGNED) || 
      getEncoding().equals(Encoding.PCM_UNSIGNED)) && (
      getSampleSizeInBits() > 8 || 
      getSampleSizeInBits() == -1))
      if (isBigEndian()) {
        str7 = "big-endian";
      } else {
        str7 = "little-endian";
      }  
    return str1 + str2 + str3 + str4 + str5 + str6 + str7;
  }
  
  public static class AudioFormat {}
}
