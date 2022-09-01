package org.apache.logging.log4j.core.script;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.AccessController;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.status.StatusLogger;

public class ScriptManager implements FileWatcher, Serializable {
  private static final long serialVersionUID = -2534169384971965196L;
  
  private static final String KEY_THREADING = "THREADING";
  
  private static final Logger logger = (Logger)StatusLogger.getLogger();
  
  private final Configuration configuration;
  
  private final ScriptEngineManager manager = new ScriptEngineManager();
  
  private final ConcurrentMap<String, ScriptRunner> scriptRunners = new ConcurrentHashMap<>();
  
  private final String languages;
  
  private final WatchManager watchManager;
  
  public ScriptManager(Configuration configuration, WatchManager watchManager) {
    this.configuration = configuration;
    this.watchManager = watchManager;
    List<ScriptEngineFactory> factories = this.manager.getEngineFactories();
    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder();
      int factorySize = factories.size();
      logger.debug("Installed {} script engine{}", Integer.valueOf(factorySize), (factorySize != 1) ? "s" : "");
      for (ScriptEngineFactory factory : factories) {
        String threading = Objects.toString(factory.getParameter("THREADING"), null);
        if (threading == null)
          threading = "Not Thread Safe"; 
        StringBuilder names = new StringBuilder();
        List<String> languageNames = factory.getNames();
        for (String name : languageNames) {
          if (names.length() > 0)
            names.append(", "); 
          names.append(name);
        } 
        if (sb.length() > 0)
          sb.append(", "); 
        sb.append(names);
        boolean compiled = factory.getScriptEngine() instanceof javax.script.Compilable;
        logger.debug("{} version: {}, language: {}, threading: {}, compile: {}, names: {}, factory class: {}", factory
            .getEngineName(), factory.getEngineVersion(), factory.getLanguageName(), threading, 
            Boolean.valueOf(compiled), languageNames, factory.getClass().getName());
      } 
      this.languages = sb.toString();
    } else {
      StringBuilder names = new StringBuilder();
      for (ScriptEngineFactory factory : factories) {
        for (String name : factory.getNames()) {
          if (names.length() > 0)
            names.append(", "); 
          names.append(name);
        } 
      } 
      this.languages = names.toString();
    } 
  }
  
  public void addScript(AbstractScript script) {
    ScriptEngine engine = this.manager.getEngineByName(script.getLanguage());
    if (engine == null) {
      logger.error("No ScriptEngine found for language " + script.getLanguage() + ". Available languages are: " + this.languages);
      return;
    } 
    if (engine.getFactory().getParameter("THREADING") == null) {
      this.scriptRunners.put(script.getName(), new ThreadLocalScriptRunner(this, script));
    } else {
      this.scriptRunners.put(script.getName(), new MainScriptRunner(this, engine, script));
    } 
    if (script instanceof ScriptFile) {
      ScriptFile scriptFile = (ScriptFile)script;
      Path path = scriptFile.getPath();
      if (scriptFile.isWatched() && path != null)
        this.watchManager.watchFile(path.toFile(), (FileWatcher)this); 
    } 
  }
  
  public Bindings createBindings(AbstractScript script) {
    return getScriptRunner(script).createBindings();
  }
  
  public AbstractScript getScript(String name) {
    ScriptRunner runner = this.scriptRunners.get(name);
    return (runner != null) ? runner.getScript() : null;
  }
  
  public void fileModified(File file) {
    ScriptRunner runner = this.scriptRunners.get(file.toString());
    if (runner == null) {
      logger.info("{} is not a running script", file.getName());
      return;
    } 
    ScriptEngine engine = runner.getScriptEngine();
    AbstractScript script = runner.getScript();
    if (engine.getFactory().getParameter("THREADING") == null) {
      this.scriptRunners.put(script.getName(), new ThreadLocalScriptRunner(this, script));
    } else {
      this.scriptRunners.put(script.getName(), new MainScriptRunner(this, engine, script));
    } 
  }
  
  public Object execute(String name, Bindings bindings) {
    ScriptRunner scriptRunner = this.scriptRunners.get(name);
    if (scriptRunner == null) {
      logger.warn("No script named {} could be found", name);
      return null;
    } 
    return AccessController.doPrivileged(() -> scriptRunner.execute(bindings));
  }
  
  private ScriptRunner getScriptRunner(AbstractScript script) {
    return this.scriptRunners.get(script.getName());
  }
  
  private class ScriptManager {}
  
  private class ScriptManager {}
  
  private static interface ScriptManager {}
  
  private abstract class ScriptManager {}
}
