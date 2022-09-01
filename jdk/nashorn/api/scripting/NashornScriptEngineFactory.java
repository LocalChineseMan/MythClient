package jdk.nashorn.api.scripting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.Version;

public final class NashornScriptEngineFactory implements ScriptEngineFactory {
  public String getEngineName() {
    return (String)getParameter("javax.script.engine");
  }
  
  public String getEngineVersion() {
    return (String)getParameter("javax.script.engine_version");
  }
  
  public List<String> getExtensions() {
    return Collections.unmodifiableList(extensions);
  }
  
  public String getLanguageName() {
    return (String)getParameter("javax.script.language");
  }
  
  public String getLanguageVersion() {
    return (String)getParameter("javax.script.language_version");
  }
  
  public String getMethodCallSyntax(String obj, String method, String... args) {
    StringBuilder sb = (new StringBuilder()).append(obj).append('.').append(method).append('(');
    int len = args.length;
    if (len > 0)
      sb.append(args[0]); 
    for (int i = 1; i < len; i++)
      sb.append(',').append(args[i]); 
    sb.append(')');
    return sb.toString();
  }
  
  public List<String> getMimeTypes() {
    return Collections.unmodifiableList(mimeTypes);
  }
  
  public List<String> getNames() {
    return Collections.unmodifiableList(names);
  }
  
  public String getOutputStatement(String toDisplay) {
    return "print(" + toDisplay + ")";
  }
  
  public Object getParameter(String key) {
    switch (key) {
      case "javax.script.name":
        return "javascript";
      case "javax.script.engine":
        return "Oracle Nashorn";
      case "javax.script.engine_version":
        return Version.version();
      case "javax.script.language":
        return "ECMAScript";
      case "javax.script.language_version":
        return "ECMA - 262 Edition 5.1";
      case "THREADING":
        return null;
    } 
    throw new IllegalArgumentException("Invalid key");
  }
  
  public String getProgram(String... statements) {
    StringBuilder sb = new StringBuilder();
    for (String statement : statements)
      sb.append(statement).append(';'); 
    return sb.toString();
  }
  
  private static final String[] DEFAULT_OPTIONS = new String[] { "-doe" };
  
  public ScriptEngine getScriptEngine() {
    try {
      return (ScriptEngine)new NashornScriptEngine(this, DEFAULT_OPTIONS, getAppClassLoader(), null);
    } catch (RuntimeException e) {
      if (Context.DEBUG)
        e.printStackTrace(); 
      throw e;
    } 
  }
  
  public ScriptEngine getScriptEngine(ClassLoader appLoader) {
    return newEngine(DEFAULT_OPTIONS, appLoader, null);
  }
  
  public ScriptEngine getScriptEngine(ClassFilter classFilter) {
    classFilter.getClass();
    return newEngine(DEFAULT_OPTIONS, getAppClassLoader(), classFilter);
  }
  
  public ScriptEngine getScriptEngine(String... args) {
    args.getClass();
    return newEngine(args, getAppClassLoader(), null);
  }
  
  public ScriptEngine getScriptEngine(String[] args, ClassLoader appLoader) {
    args.getClass();
    return newEngine(args, appLoader, null);
  }
  
  public ScriptEngine getScriptEngine(String[] args, ClassLoader appLoader, ClassFilter classFilter) {
    args.getClass();
    classFilter.getClass();
    return newEngine(args, appLoader, classFilter);
  }
  
  private ScriptEngine newEngine(String[] args, ClassLoader appLoader, ClassFilter classFilter) {
    checkConfigPermission();
    try {
      return (ScriptEngine)new NashornScriptEngine(this, args, appLoader, classFilter);
    } catch (RuntimeException e) {
      if (Context.DEBUG)
        e.printStackTrace(); 
      throw e;
    } 
  }
  
  private static void checkConfigPermission() {
    SecurityManager sm = System.getSecurityManager();
    if (sm != null)
      sm.checkPermission(new RuntimePermission("nashorn.setConfig")); 
  }
  
  private static final List<String> names = immutableList(new String[] { "nashorn", "Nashorn", "js", "JS", "JavaScript", "javascript", "ECMAScript", "ecmascript" });
  
  private static final List<String> mimeTypes = immutableList(new String[] { "application/javascript", "application/ecmascript", "text/javascript", "text/ecmascript" });
  
  private static final List<String> extensions = immutableList(new String[] { "js" });
  
  private static List<String> immutableList(String... elements) {
    return Collections.unmodifiableList(Arrays.asList(elements));
  }
  
  private static ClassLoader getAppClassLoader() {
    ClassLoader ccl = Thread.currentThread().getContextClassLoader();
    return (ccl == null) ? NashornScriptEngineFactory.class.getClassLoader() : ccl;
  }
}
