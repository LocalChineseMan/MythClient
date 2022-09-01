package paulscode.sound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.AudioFormat;

public class Library {
  private SoundSystemLogger logger;
  
  protected ListenerData listener;
  
  protected HashMap<String, SoundBuffer> bufferMap = null;
  
  protected HashMap<String, Source> sourceMap;
  
  private MidiChannel midiChannel;
  
  protected List<Channel> streamingChannels;
  
  protected List<Channel> normalChannels;
  
  private String[] streamingChannelSourceNames;
  
  private String[] normalChannelSourceNames;
  
  private int nextStreamingChannel = 0;
  
  private int nextNormalChannel = 0;
  
  protected StreamThread streamThread;
  
  protected boolean reverseByteOrder = false;
  
  public Library() throws SoundSystemException {
    this.logger = SoundSystemConfig.getLogger();
    this.bufferMap = new HashMap<String, SoundBuffer>();
    this.sourceMap = new HashMap<String, Source>();
    this.listener = new ListenerData(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 0.0F);
    this.streamingChannels = new LinkedList<Channel>();
    this.normalChannels = new LinkedList<Channel>();
    this.streamingChannelSourceNames = new String[SoundSystemConfig.getNumberStreamingChannels()];
    this.normalChannelSourceNames = new String[SoundSystemConfig.getNumberNormalChannels()];
    this.streamThread = new StreamThread();
    this.streamThread.start();
  }
  
  public void cleanup() {
    this.streamThread.kill();
    this.streamThread.interrupt();
    for (int i = 0; i < 50; i++) {
      if (!this.streamThread.alive())
        break; 
      try {
        Thread.sleep(100L);
      } catch (Exception e) {}
    } 
    if (this.streamThread.alive()) {
      errorMessage("Stream thread did not die!");
      message("Ignoring errors... continuing clean-up.");
    } 
    if (this.midiChannel != null) {
      this.midiChannel.cleanup();
      this.midiChannel = null;
    } 
    Channel channel = null;
    if (this.streamingChannels != null) {
      while (!this.streamingChannels.isEmpty()) {
        channel = this.streamingChannels.remove(0);
        channel.close();
        channel.cleanup();
        channel = null;
      } 
      this.streamingChannels.clear();
      this.streamingChannels = null;
    } 
    if (this.normalChannels != null) {
      while (!this.normalChannels.isEmpty()) {
        channel = this.normalChannels.remove(0);
        channel.close();
        channel.cleanup();
        channel = null;
      } 
      this.normalChannels.clear();
      this.normalChannels = null;
    } 
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        source.cleanup(); 
    } 
    this.sourceMap.clear();
    this.sourceMap = null;
    this.listener = null;
    this.streamThread = null;
  }
  
  public void init() throws SoundSystemException {
    Channel channel = null;
    int x;
    for (x = 0; x < SoundSystemConfig.getNumberStreamingChannels(); x++) {
      channel = createChannel(1);
      if (channel == null)
        break; 
      this.streamingChannels.add(channel);
    } 
    for (x = 0; x < SoundSystemConfig.getNumberNormalChannels(); x++) {
      channel = createChannel(0);
      if (channel == null)
        break; 
      this.normalChannels.add(channel);
    } 
  }
  
  public static boolean libraryCompatible() {
    return true;
  }
  
  protected Channel createChannel(int type) {
    return new Channel(type);
  }
  
  public boolean loadSound(FilenameURL filenameURL) {
    return true;
  }
  
  public boolean loadSound(SoundBuffer buffer, String identifier) {
    return true;
  }
  
  public LinkedList<String> getAllLoadedFilenames() {
    LinkedList<String> filenames = new LinkedList<String>();
    Set<String> keys = this.bufferMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext())
      filenames.add(iter.next()); 
    return filenames;
  }
  
  public LinkedList<String> getAllSourcenames() {
    LinkedList<String> sourcenames = new LinkedList<String>();
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    if (this.midiChannel != null)
      sourcenames.add(this.midiChannel.getSourcename()); 
    while (iter.hasNext())
      sourcenames.add(iter.next()); 
    return sourcenames;
  }
  
  public void unloadSound(String filename) {
    this.bufferMap.remove(filename);
  }
  
  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float posX, float posY, float posZ, int attModel, float distOrRoll) {
    this.sourceMap.put(sourcename, new Source(audioFormat, priority, sourcename, posX, posY, posZ, attModel, distOrRoll));
  }
  
  public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float posX, float posY, float posZ, int attModel, float distOrRoll) {
    this.sourceMap.put(sourcename, new Source(priority, toStream, toLoop, sourcename, filenameURL, null, posX, posY, posZ, attModel, distOrRoll, false));
  }
  
  public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float posX, float posY, float posZ, int attModel, float distOrRoll, boolean tmp) {
    this.sourceMap.put(sourcename, new Source(priority, toStream, toLoop, sourcename, filenameURL, null, posX, posY, posZ, attModel, distOrRoll, tmp));
  }
  
  public void setTemporary(String sourcename, boolean temporary) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setTemporary(temporary); 
  }
  
  public void setPosition(String sourcename, float x, float y, float z) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setPosition(x, y, z); 
  }
  
  public void setPriority(String sourcename, boolean pri) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setPriority(pri); 
  }
  
  public void setLooping(String sourcename, boolean lp) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setLooping(lp); 
  }
  
  public void setAttenuation(String sourcename, int model) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setAttenuation(model); 
  }
  
  public void setDistOrRoll(String sourcename, float dr) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setDistOrRoll(dr); 
  }
  
  public void setVelocity(String sourcename, float x, float y, float z) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.setVelocity(x, y, z); 
  }
  
  public void setListenerVelocity(float x, float y, float z) {
    this.listener.setVelocity(x, y, z);
  }
  
  public void dopplerChanged() {}
  
  public float millisecondsPlayed(String sourcename) {
    if (sourcename == null || sourcename.equals("")) {
      errorMessage("Sourcename not specified in method 'millisecondsPlayed'");
      return -1.0F;
    } 
    if (midiSourcename(sourcename)) {
      errorMessage("Unable to calculate milliseconds for MIDI source.");
      return -1.0F;
    } 
    Source source = this.sourceMap.get(sourcename);
    if (source == null)
      errorMessage("Source '" + sourcename + "' not found in " + "method 'millisecondsPlayed'"); 
    return source.millisecondsPlayed();
  }
  
  public int feedRawAudioData(String sourcename, byte[] buffer) {
    if (sourcename == null || sourcename.equals("")) {
      errorMessage("Sourcename not specified in method 'feedRawAudioData'");
      return -1;
    } 
    if (midiSourcename(sourcename)) {
      errorMessage("Raw audio data can not be fed to the MIDI channel.");
      return -1;
    } 
    Source source = this.sourceMap.get(sourcename);
    if (source == null)
      errorMessage("Source '" + sourcename + "' not found in " + "method 'feedRawAudioData'"); 
    return feedRawAudioData(source, buffer);
  }
  
  public int feedRawAudioData(Source source, byte[] buffer) {
    if (source == null) {
      errorMessage("Source parameter null in method 'feedRawAudioData'");
      return -1;
    } 
    if (!source.toStream) {
      errorMessage("Only a streaming source may be specified in method 'feedRawAudioData'");
      return -1;
    } 
    if (!source.rawDataStream) {
      errorMessage("Streaming source already associated with a file or URL in method'feedRawAudioData'");
      return -1;
    } 
    if (!source.playing() || source.channel == null) {
      Channel channel;
      if (source.channel != null && source.channel.attachedSource == source) {
        channel = source.channel;
      } else {
        channel = getNextChannel(source);
      } 
      int processed = source.feedRawAudioData(channel, buffer);
      channel.attachedSource = source;
      this.streamThread.watch(source);
      this.streamThread.interrupt();
      return processed;
    } 
    return source.feedRawAudioData(source.channel, buffer);
  }
  
  public void play(String sourcename) {
    if (sourcename == null || sourcename.equals("")) {
      errorMessage("Sourcename not specified in method 'play'");
      return;
    } 
    if (midiSourcename(sourcename)) {
      this.midiChannel.play();
    } else {
      Source source = this.sourceMap.get(sourcename);
      if (source == null)
        errorMessage("Source '" + sourcename + "' not found in " + "method 'play'"); 
      play(source);
    } 
  }
  
  public void play(Source source) {
    if (source == null)
      return; 
    if (source.rawDataStream)
      return; 
    if (!source.active())
      return; 
    if (!source.playing()) {
      Channel channel = getNextChannel(source);
      if (source != null && channel != null) {
        if (source.channel != null && source.channel.attachedSource != source)
          source.channel = null; 
        channel.attachedSource = source;
        source.play(channel);
        if (source.toStream) {
          this.streamThread.watch(source);
          this.streamThread.interrupt();
        } 
      } 
    } 
  }
  
  public void stop(String sourcename) {
    if (sourcename == null || sourcename.equals("")) {
      errorMessage("Sourcename not specified in method 'stop'");
      return;
    } 
    if (midiSourcename(sourcename)) {
      this.midiChannel.stop();
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.stop(); 
    } 
  }
  
  public void pause(String sourcename) {
    if (sourcename == null || sourcename.equals("")) {
      errorMessage("Sourcename not specified in method 'stop'");
      return;
    } 
    if (midiSourcename(sourcename)) {
      this.midiChannel.pause();
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.pause(); 
    } 
  }
  
  public void rewind(String sourcename) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.rewind();
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.rewind(); 
    } 
  }
  
  public void flush(String sourcename) {
    if (midiSourcename(sourcename)) {
      errorMessage("You can not flush the MIDI channel");
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.flush(); 
    } 
  }
  
  public void cull(String sourcename) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.cull(); 
  }
  
  public void activate(String sourcename) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.activate();
      if (mySource.toPlay)
        play(mySource); 
    } 
  }
  
  public void setMasterVolume(float value) {
    SoundSystemConfig.setMasterGain(value);
    if (this.midiChannel != null)
      this.midiChannel.resetGain(); 
  }
  
  public void setVolume(String sourcename, float value) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.setVolume(value);
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null) {
        float newVolume = value;
        if (newVolume < 0.0F) {
          newVolume = 0.0F;
        } else if (newVolume > 1.0F) {
          newVolume = 1.0F;
        } 
        mySource.sourceVolume = newVolume;
        mySource.positionChanged();
      } 
    } 
  }
  
  public float getVolume(String sourcename) {
    if (midiSourcename(sourcename))
      return this.midiChannel.getVolume(); 
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      return mySource.sourceVolume; 
    return 0.0F;
  }
  
  public void setPitch(String sourcename, float value) {
    if (!midiSourcename(sourcename)) {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null) {
        float newPitch = value;
        if (newPitch < 0.5F) {
          newPitch = 0.5F;
        } else if (newPitch > 2.0F) {
          newPitch = 2.0F;
        } 
        mySource.setPitch(newPitch);
        mySource.positionChanged();
      } 
    } 
  }
  
  public float getPitch(String sourcename) {
    if (!midiSourcename(sourcename)) {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        return mySource.getPitch(); 
    } 
    return 1.0F;
  }
  
  public void moveListener(float x, float y, float z) {
    setListenerPosition(this.listener.position.x + x, this.listener.position.y + y, this.listener.position.z + z);
  }
  
  public void setListenerPosition(float x, float y, float z) {
    this.listener.setPosition(x, y, z);
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        source.positionChanged(); 
    } 
  }
  
  public void turnListener(float angle) {
    setListenerAngle(this.listener.angle + angle);
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        source.positionChanged(); 
    } 
  }
  
  public void setListenerAngle(float angle) {
    this.listener.setAngle(angle);
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        source.positionChanged(); 
    } 
  }
  
  public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ) {
    this.listener.setOrientation(lookX, lookY, lookZ, upX, upY, upZ);
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        source.positionChanged(); 
    } 
  }
  
  public void setListenerData(ListenerData l) {
    this.listener.setData(l);
  }
  
  public void copySources(HashMap<String, Source> srcMap) {
    if (srcMap == null)
      return; 
    Set<String> keys = srcMap.keySet();
    Iterator<String> iter = keys.iterator();
    this.sourceMap.clear();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source srcData = srcMap.get(sourcename);
      if (srcData != null) {
        loadSound(srcData.filenameURL);
        this.sourceMap.put(sourcename, new Source(srcData, null));
      } 
    } 
  }
  
  public void removeSource(String sourcename) {
    Source mySource = this.sourceMap.get(sourcename);
    if (mySource != null)
      mySource.cleanup(); 
    this.sourceMap.remove(sourcename);
  }
  
  public void removeTemporarySources() {
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source srcData = this.sourceMap.get(sourcename);
      if (srcData != null && srcData.temporary && !srcData.playing()) {
        srcData.cleanup();
        iter.remove();
      } 
    } 
  }
  
  private Channel getNextChannel(Source source) {
    int nextChannel;
    List<Channel> channelList;
    String[] sourceNames;
    if (source == null)
      return null; 
    String sourcename = source.sourcename;
    if (sourcename == null)
      return null; 
    if (source.toStream) {
      nextChannel = this.nextStreamingChannel;
      channelList = this.streamingChannels;
      sourceNames = this.streamingChannelSourceNames;
    } else {
      nextChannel = this.nextNormalChannel;
      channelList = this.normalChannels;
      sourceNames = this.normalChannelSourceNames;
    } 
    int channels = channelList.size();
    int x;
    for (x = 0; x < channels; x++) {
      if (sourcename.equals(sourceNames[x]))
        return channelList.get(x); 
    } 
    int n = nextChannel;
    for (x = 0; x < channels; x++) {
      Source src;
      String name = sourceNames[n];
      if (name == null) {
        src = null;
      } else {
        src = this.sourceMap.get(name);
      } 
      if (src == null || !src.playing()) {
        if (source.toStream) {
          this.nextStreamingChannel = n + 1;
          if (this.nextStreamingChannel >= channels)
            this.nextStreamingChannel = 0; 
        } else {
          this.nextNormalChannel = n + 1;
          if (this.nextNormalChannel >= channels)
            this.nextNormalChannel = 0; 
        } 
        sourceNames[n] = sourcename;
        return channelList.get(n);
      } 
      n++;
      if (n >= channels)
        n = 0; 
    } 
    n = nextChannel;
    for (x = 0; x < channels; x++) {
      Source src;
      String name = sourceNames[n];
      if (name == null) {
        src = null;
      } else {
        src = this.sourceMap.get(name);
      } 
      if (src == null || !src.playing() || !src.priority) {
        if (source.toStream) {
          this.nextStreamingChannel = n + 1;
          if (this.nextStreamingChannel >= channels)
            this.nextStreamingChannel = 0; 
        } else {
          this.nextNormalChannel = n + 1;
          if (this.nextNormalChannel >= channels)
            this.nextNormalChannel = 0; 
        } 
        sourceNames[n] = sourcename;
        return channelList.get(n);
      } 
      n++;
      if (n >= channels)
        n = 0; 
    } 
    return null;
  }
  
  public void replaySources() {
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = this.sourceMap.get(sourcename);
      if (source != null)
        if (source.toPlay && !source.playing()) {
          play(sourcename);
          source.toPlay = false;
        }  
    } 
  }
  
  public void queueSound(String sourcename, FilenameURL filenameURL) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.queueSound(filenameURL);
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.queueSound(filenameURL); 
    } 
  }
  
  public void dequeueSound(String sourcename, String filename) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.dequeueSound(filename);
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.dequeueSound(filename); 
    } 
  }
  
  public void fadeOut(String sourcename, FilenameURL filenameURL, long milis) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.fadeOut(filenameURL, milis);
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.fadeOut(filenameURL, milis); 
    } 
  }
  
  public void fadeOutIn(String sourcename, FilenameURL filenameURL, long milisOut, long milisIn) {
    if (midiSourcename(sourcename)) {
      this.midiChannel.fadeOutIn(filenameURL, milisOut, milisIn);
    } else {
      Source mySource = this.sourceMap.get(sourcename);
      if (mySource != null)
        mySource.fadeOutIn(filenameURL, milisOut, milisIn); 
    } 
  }
  
  public void checkFadeVolumes() {
    if (this.midiChannel != null)
      this.midiChannel.resetGain(); 
    for (int x = 0; x < this.streamingChannels.size(); x++) {
      Channel channel = this.streamingChannels.get(x);
      if (channel != null) {
        Source source = channel.attachedSource;
        if (source != null)
          source.checkFadeOut(); 
      } 
    } 
    Channel c = null;
    Source s = null;
  }
  
  public void loadMidi(boolean toLoop, String sourcename, FilenameURL filenameURL) {
    if (filenameURL == null) {
      errorMessage("Filename/URL not specified in method 'loadMidi'.");
      return;
    } 
    if (!filenameURL.getFilename().matches(SoundSystemConfig.EXTENSION_MIDI)) {
      errorMessage("Filename/identifier doesn't end in '.mid' or'.midi' in method loadMidi.");
      return;
    } 
    if (this.midiChannel == null) {
      this.midiChannel = new MidiChannel(toLoop, sourcename, filenameURL);
    } else {
      this.midiChannel.switchSource(toLoop, sourcename, filenameURL);
    } 
  }
  
  public void unloadMidi() {
    if (this.midiChannel != null)
      this.midiChannel.cleanup(); 
    this.midiChannel = null;
  }
  
  public boolean midiSourcename(String sourcename) {
    if (this.midiChannel == null || sourcename == null)
      return false; 
    if (this.midiChannel.getSourcename() == null || sourcename.equals(""))
      return false; 
    if (sourcename.equals(this.midiChannel.getSourcename()))
      return true; 
    return false;
  }
  
  public Source getSource(String sourcename) {
    return this.sourceMap.get(sourcename);
  }
  
  public MidiChannel getMidiChannel() {
    return this.midiChannel;
  }
  
  public void setMidiChannel(MidiChannel c) {
    if (this.midiChannel != null && this.midiChannel != c)
      this.midiChannel.cleanup(); 
    this.midiChannel = c;
  }
  
  public void listenerMoved() {
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source srcData = this.sourceMap.get(sourcename);
      if (srcData != null)
        srcData.listenerMoved(); 
    } 
  }
  
  public HashMap<String, Source> getSources() {
    return this.sourceMap;
  }
  
  public ListenerData getListenerData() {
    return this.listener;
  }
  
  public boolean reverseByteOrder() {
    return this.reverseByteOrder;
  }
  
  public static String getTitle() {
    return "No Sound";
  }
  
  public static String getDescription() {
    return "Silent Mode";
  }
  
  public String getClassName() {
    return "Library";
  }
  
  protected void message(String message) {
    this.logger.message(message, 0);
  }
  
  protected void importantMessage(String message) {
    this.logger.importantMessage(message, 0);
  }
  
  protected boolean errorCheck(boolean error, String message) {
    return this.logger.errorCheck(error, getClassName(), message, 0);
  }
  
  protected void errorMessage(String message) {
    this.logger.errorMessage(getClassName(), message, 0);
  }
  
  protected void printStackTrace(Exception e) {
    this.logger.printStackTrace(e, 1);
  }
}
