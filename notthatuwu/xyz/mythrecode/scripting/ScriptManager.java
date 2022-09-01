package notthatuwu.xyz.mythrecode.scripting;

import java.io.File;
import java.util.HashMap;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

public class ScriptManager {
  public File scriptFolder = new File(Client.INSTANCE.mythFolder + "/scripts");
  
  public File getScriptFolder() {
    return this.scriptFolder;
  }
  
  public HashMap<String, Script> scripts = new HashMap<>();
  
  public HashMap<String, Script> getScripts() {
    return this.scripts;
  }
  
  public ScriptManager() {
    loadScripts();
  }
  
  public void loadScripts() {
    if (!this.scriptFolder.exists())
      this.scriptFolder.mkdir(); 
    for (File file : this.scriptFolder.listFiles()) {
      if (file.getName().endsWith(".js"))
        try {
          Script script = new Script(file);
          this.scripts.put(script.getName(), script);
        } catch (Exception e) {
          e.printStackTrace();
        }  
    } 
  }
  
  public void reloadScripts() {
    this.scripts.clear();
    loadScripts();
  }
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    invoke("update", new Object[] { event });
  }
  
  @EventTarget
  public void onMove(EventMove event) {
    invoke("move", new Object[] { event });
  }
  
  @EventTarget
  public void on2D(Event2D event) {
    invoke("render2D", new Object[] { event });
  }
  
  @EventTarget
  public void onSendPacket(EventSendPacket event) {
    invoke("sendPacket", new Object[] { event });
  }
  
  @EventTarget
  public void onReceivePacket(EventReceivePacket event) {
    invoke("receivePacket", new Object[] { event });
  }
  
  public void invoke(String event, Object... args) {
    this.scripts.forEach((name, script) -> script.invoke(event, args));
  }
}
