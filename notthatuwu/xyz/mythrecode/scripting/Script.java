package notthatuwu.xyz.mythrecode.scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.interfaces.IMethods;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.utils.FileUtil;
import notthatuwu.xyz.mythrecode.scripting.api.ClientFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.MoveFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.PacketFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.PlayerFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.RenderFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.SettingFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.ShaderFunctions;
import notthatuwu.xyz.mythrecode.scripting.api.WorldFunctions;

public class Script implements IMethods {
  private final ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("nashorn");
  
  private String name;
  
  private String author;
  
  private Category category;
  
  private Module module;
  
  public ArrayList<Setting> settings;
  
  public ScriptEngine getEngine() {
    return this.engine;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getAuthor() {
    return this.author;
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public Module getModule() {
    return this.module;
  }
  
  public ArrayList<Setting> getSettings() {
    return this.settings;
  }
  
  private HashMap<String, JSObject> events = new HashMap<>();
  
  public HashMap<String, JSObject> getEvents() {
    return this.events;
  }
  
  public Script(File scriptFile) throws ScriptException {
    String scriptContent = FileUtil.readFile(scriptFile);
    this.settings = new ArrayList<>();
    register();
    try {
      this.engine.eval(scriptContent);
    } catch (ScriptException ignored) {
      ignored.printStackTrace();
    } 
    this.name = this.engine.get("name").toString();
    this.author = this.engine.get("author").toString();
    this.category = Category.valueOf(this.engine.get("category").toString().toUpperCase());
    this.module = (Module)new Object(this, this.name, "", this.category, 0);
    this.module.settings.addAll(this.settings);
    Client.INSTANCE.moduleManager.getModuleHashMap().put(Module.class, this.module);
  }
  
  public void on(String event, JSObject function) {
    this.events.put(event, function);
  }
  
  public void invoke(String event, Object... args) {
    if (!this.module.isEnabled())
      return; 
    if (this.events.containsKey(event))
      ((JSObject)this.events.get(event)).call("", args); 
  }
  
  private void register() {
    this.engine.put("move", new MoveFunctions());
    this.engine.put("render", new RenderFunctions());
    this.engine.put("setting", new SettingFunctions(this));
    this.engine.put("shader", new ShaderFunctions());
    this.engine.put("packet", new PacketFunctions());
    this.engine.put("player", new PlayerFunctions());
    this.engine.put("world", new WorldFunctions());
    this.engine.put("client", new ClientFunctions());
    this.engine.put("script", this);
  }
}
