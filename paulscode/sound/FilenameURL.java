package paulscode.sound;

import java.net.URL;

public class FilenameURL {
  private SoundSystemLogger logger;
  
  private String filename = null;
  
  private URL url = null;
  
  public FilenameURL(URL url, String identifier) {
    this.logger = SoundSystemConfig.getLogger();
    this.filename = identifier;
    this.url = url;
  }
  
  public FilenameURL(String filename) {
    this.logger = SoundSystemConfig.getLogger();
    this.filename = filename;
    this.url = null;
  }
  
  public String getFilename() {
    return this.filename;
  }
  
  public URL getURL() {
    if (this.url == null)
      if (this.filename.matches(SoundSystemConfig.PREFIX_URL)) {
        try {
          this.url = new URL(this.filename);
        } catch (Exception e) {
          errorMessage("Unable to access online URL in method 'getURL'");
          printStackTrace(e);
          return null;
        } 
      } else {
        this.url = getClass().getClassLoader().getResource(SoundSystemConfig.getSoundFilesPackage() + this.filename);
      }  
    return this.url;
  }
  
  private void errorMessage(String message) {
    this.logger.errorMessage("MidiChannel", message, 0);
  }
  
  private void printStackTrace(Exception e) {
    this.logger.printStackTrace(e, 1);
  }
}
