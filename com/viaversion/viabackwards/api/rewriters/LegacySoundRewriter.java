package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;

@Deprecated
public abstract class LegacySoundRewriter<T extends BackwardsProtocol> extends RewriterBase<T> {
  protected final Int2ObjectMap<SoundData> soundRewrites = (Int2ObjectMap<SoundData>)new Int2ObjectOpenHashMap(64);
  
  protected LegacySoundRewriter(T protocol) {
    super((Protocol)protocol);
  }
  
  public SoundData added(int id, int replacement) {
    return added(id, replacement, -1.0F);
  }
  
  public SoundData added(int id, int replacement, float newPitch) {
    SoundData data = new SoundData(replacement, true, newPitch, true);
    this.soundRewrites.put(id, data);
    return data;
  }
  
  public SoundData removed(int id) {
    SoundData data = new SoundData(-1, false, -1.0F, false);
    this.soundRewrites.put(id, data);
    return data;
  }
  
  public int handleSounds(int soundId) {
    int newSoundId = soundId;
    SoundData data = (SoundData)this.soundRewrites.get(soundId);
    if (data != null)
      return data.getReplacementSound(); 
    for (ObjectIterator<Int2ObjectMap.Entry<SoundData>> objectIterator = this.soundRewrites.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) {
      Int2ObjectMap.Entry<SoundData> entry = objectIterator.next();
      if (soundId > entry.getIntKey()) {
        if (((SoundData)entry.getValue()).isAdded()) {
          newSoundId--;
          continue;
        } 
        newSoundId++;
      } 
    } 
    return newSoundId;
  }
  
  public boolean hasPitch(int soundId) {
    SoundData data = (SoundData)this.soundRewrites.get(soundId);
    return (data != null && data.isChangePitch());
  }
  
  public float handlePitch(int soundId) {
    SoundData data = (SoundData)this.soundRewrites.get(soundId);
    return (data != null) ? data.getNewPitch() : 1.0F;
  }
  
  public static final class SoundData {
    private final int replacementSound;
    
    private final boolean changePitch;
    
    private final float newPitch;
    
    private final boolean added;
    
    public SoundData(int replacementSound, boolean changePitch, float newPitch, boolean added) {
      this.replacementSound = replacementSound;
      this.changePitch = changePitch;
      this.newPitch = newPitch;
      this.added = added;
    }
    
    public int getReplacementSound() {
      return this.replacementSound;
    }
    
    public boolean isChangePitch() {
      return this.changePitch;
    }
    
    public float getNewPitch() {
      return this.newPitch;
    }
    
    public boolean isAdded() {
      return this.added;
    }
  }
}
