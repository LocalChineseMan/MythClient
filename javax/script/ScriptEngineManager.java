package javax.script;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ScriptEngineManager {
  private static final boolean DEBUG = false;
  
  private HashSet<ScriptEngineFactory> engineSpis;
  
  private HashMap<String, ScriptEngineFactory> nameAssociations;
  
  private HashMap<String, ScriptEngineFactory> extensionAssociations;
  
  private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
  
  private Bindings globalScope;
  
  public ScriptEngineManager() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    init(classLoader);
  }
  
  public ScriptEngineManager(ClassLoader paramClassLoader) {
    init(paramClassLoader);
  }
  
  private void init(ClassLoader paramClassLoader) {
    this.globalScope = new SimpleBindings();
    this.engineSpis = new HashSet<>();
    this.nameAssociations = new HashMap<>();
    this.extensionAssociations = new HashMap<>();
    this.mimeTypeAssociations = new HashMap<>();
    initEngines(paramClassLoader);
  }
  
  private ServiceLoader<ScriptEngineFactory> getServiceLoader(ClassLoader paramClassLoader) {
    if (paramClassLoader != null)
      return ServiceLoader.load(ScriptEngineFactory.class, paramClassLoader); 
    return ServiceLoader.loadInstalled(ScriptEngineFactory.class);
  }
  
  private void initEngines(final ClassLoader loader) {
    Iterator<ScriptEngineFactory> iterator = null;
    try {
      ServiceLoader serviceLoader = AccessController.<ServiceLoader>doPrivileged((PrivilegedAction)new PrivilegedAction<ServiceLoader<ScriptEngineFactory>>() {
            public ServiceLoader<ScriptEngineFactory> run() {
              return ScriptEngineManager.this.getServiceLoader(loader);
            }
          });
      iterator = serviceLoader.iterator();
    } catch (ServiceConfigurationError serviceConfigurationError) {
      System.err.println("Can't find ScriptEngineFactory providers: " + serviceConfigurationError
          .getMessage());
      return;
    } 
    try {
      while (iterator.hasNext()) {
        try {
          ScriptEngineFactory scriptEngineFactory = iterator.next();
          this.engineSpis.add(scriptEngineFactory);
        } catch (ServiceConfigurationError serviceConfigurationError) {
          System.err.println("ScriptEngineManager providers.next(): " + serviceConfigurationError
              .getMessage());
        } 
      } 
    } catch (ServiceConfigurationError serviceConfigurationError) {
      System.err.println("ScriptEngineManager providers.hasNext(): " + serviceConfigurationError
          .getMessage());
      return;
    } 
  }
  
  public void setBindings(Bindings paramBindings) {
    if (paramBindings == null)
      throw new IllegalArgumentException("Global scope cannot be null."); 
    this.globalScope = paramBindings;
  }
  
  public Bindings getBindings() {
    return this.globalScope;
  }
  
  public void put(String paramString, Object paramObject) {
    this.globalScope.put(paramString, paramObject);
  }
  
  public Object get(String paramString) {
    return this.globalScope.get(paramString);
  }
  
  public ScriptEngine getEngineByName(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    ScriptEngineFactory scriptEngineFactory;
    if (null != (scriptEngineFactory = (ScriptEngineFactory)this.nameAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory1 = scriptEngineFactory;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory1 : this.engineSpis) {
      List<String> list = null;
      try {
        list = scriptEngineFactory1.getNames();
      } catch (Exception exception) {}
      if (list != null)
        for (String str : list) {
          if (paramString.equals(str))
            try {
              ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
              scriptEngine.setBindings(getBindings(), 200);
              return scriptEngine;
            } catch (Exception exception) {} 
        }  
    } 
    return null;
  }
  
  public ScriptEngine getEngineByExtension(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    ScriptEngineFactory scriptEngineFactory;
    if (null != (scriptEngineFactory = (ScriptEngineFactory)this.extensionAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory1 = scriptEngineFactory;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory1 : this.engineSpis) {
      List<String> list = null;
      try {
        list = scriptEngineFactory1.getExtensions();
      } catch (Exception exception) {}
      if (list == null)
        continue; 
      for (String str : list) {
        if (paramString.equals(str))
          try {
            ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
            scriptEngine.setBindings(getBindings(), 200);
            return scriptEngine;
          } catch (Exception exception) {} 
      } 
    } 
    return null;
  }
  
  public ScriptEngine getEngineByMimeType(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    ScriptEngineFactory scriptEngineFactory;
    if (null != (scriptEngineFactory = (ScriptEngineFactory)this.mimeTypeAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory1 = scriptEngineFactory;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory1 : this.engineSpis) {
      List<String> list = null;
      try {
        list = scriptEngineFactory1.getMimeTypes();
      } catch (Exception exception) {}
      if (list == null)
        continue; 
      for (String str : list) {
        if (paramString.equals(str))
          try {
            ScriptEngine scriptEngine = scriptEngineFactory1.getScriptEngine();
            scriptEngine.setBindings(getBindings(), 200);
            return scriptEngine;
          } catch (Exception exception) {} 
      } 
    } 
    return null;
  }
  
  public List<ScriptEngineFactory> getEngineFactories() {
    ArrayList<ScriptEngineFactory> arrayList = new ArrayList(this.engineSpis.size());
    for (ScriptEngineFactory scriptEngineFactory : this.engineSpis)
      arrayList.add(scriptEngineFactory); 
    return Collections.unmodifiableList(arrayList);
  }
  
  public void registerEngineName(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.nameAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineMimeType(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.mimeTypeAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineExtension(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.extensionAssociations.put(paramString, paramScriptEngineFactory);
  }
}
