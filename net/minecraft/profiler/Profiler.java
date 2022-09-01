package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import optifine.Config;
import optifine.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
  private static final Logger logger = LogManager.getLogger();
  
  private final List sectionList = Lists.newArrayList();
  
  private final List timestampList = Lists.newArrayList();
  
  public boolean profilingEnabled;
  
  private String profilingSection = "";
  
  private final Map profilingMap = Maps.newHashMap();
  
  private static final String __OBFID = "CL_00001497";
  
  public boolean profilerGlobalEnabled = true;
  
  private boolean profilerLocalEnabled;
  
  private static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
  
  private static final String TICK = "tick";
  
  private static final String PRE_RENDER_ERRORS = "preRenderErrors";
  
  private static final String RENDER = "render";
  
  private static final String DISPLAY = "display";
  
  private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
  
  private static final int HASH_TICK = "tick".hashCode();
  
  private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
  
  private static final int HASH_RENDER = "render".hashCode();
  
  private static final int HASH_DISPLAY = "display".hashCode();
  
  public Profiler() {
    this.profilerLocalEnabled = this.profilerGlobalEnabled;
  }
  
  public void clearProfiling() {
    this.profilingMap.clear();
    this.profilingSection = "";
    this.sectionList.clear();
    this.profilerLocalEnabled = this.profilerGlobalEnabled;
  }
  
  public void startSection(String name) {
    if (Lagometer.isActive()) {
      int i = name.hashCode();
      if (i == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
        Lagometer.timerScheduledExecutables.start();
      } else if (i == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
        Lagometer.timerScheduledExecutables.end();
        Lagometer.timerTick.start();
      } else if (i == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
        Lagometer.timerTick.end();
      } 
    } 
    if (Config.isFastRender()) {
      int j = name.hashCode();
      if (j == HASH_RENDER && name.equals("render")) {
        GlStateManager.clearEnabled = false;
      } else if (j == HASH_DISPLAY && name.equals("display")) {
        GlStateManager.clearEnabled = true;
      } 
    } 
    if (this.profilerLocalEnabled)
      if (this.profilingEnabled) {
        if (this.profilingSection.length() > 0)
          this.profilingSection += "."; 
        this.profilingSection += name;
        this.sectionList.add(this.profilingSection);
        this.timestampList.add(Long.valueOf(System.nanoTime()));
      }  
  }
  
  public void endSection() {
    if (this.profilerLocalEnabled)
      if (this.profilingEnabled) {
        long i = System.nanoTime();
        long j = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
        this.sectionList.remove(this.sectionList.size() - 1);
        long k = i - j;
        if (this.profilingMap.containsKey(this.profilingSection)) {
          this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + k));
        } else {
          this.profilingMap.put(this.profilingSection, Long.valueOf(k));
        } 
        if (k > 100000000L)
          logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + (k / 1000000.0D) + " ms"); 
        this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
      }  
  }
  
  public List getProfilingData(String p_76321_1_) {
    this.profilerLocalEnabled = this.profilerGlobalEnabled;
    if (!this.profilerLocalEnabled)
      return new ArrayList(Arrays.asList((Object[])new Result[] { new Result("root", 0.0D, 0.0D) })); 
    if (!this.profilingEnabled)
      return null; 
    long i = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
    long j = this.profilingMap.containsKey(p_76321_1_) ? ((Long)this.profilingMap.get(p_76321_1_)).longValue() : -1L;
    ArrayList<Result> arraylist = Lists.newArrayList();
    if (p_76321_1_.length() > 0)
      p_76321_1_ = p_76321_1_ + "."; 
    long k = 0L;
    for (Object s : this.profilingMap.keySet()) {
      if (((String)s).length() > p_76321_1_.length() && ((String)s).startsWith(p_76321_1_) && ((String)s).indexOf(".", p_76321_1_.length() + 1) < 0)
        k += ((Long)this.profilingMap.get(s)).longValue(); 
    } 
    float f = (float)k;
    if (k < j)
      k = j; 
    if (i < k)
      i = k; 
    for (Object s10 : this.profilingMap.keySet()) {
      String s1 = (String)s10;
      if (s1.length() > p_76321_1_.length() && s1.startsWith(p_76321_1_) && s1.indexOf(".", p_76321_1_.length() + 1) < 0) {
        long l = ((Long)this.profilingMap.get(s1)).longValue();
        double d0 = l * 100.0D / k;
        double d1 = l * 100.0D / i;
        String s2 = s1.substring(p_76321_1_.length());
        arraylist.add(new Result(s2, d0, d1));
      } 
    } 
    for (Object s3 : this.profilingMap.keySet())
      this.profilingMap.put(s3, Long.valueOf(((Long)this.profilingMap.get(s3)).longValue() * 950L / 1000L)); 
    if ((float)k > f)
      arraylist.add(new Result("unspecified", ((float)k - f) * 100.0D / k, ((float)k - f) * 100.0D / i)); 
    Collections.sort(arraylist);
    arraylist.add(0, new Result(p_76321_1_, 100.0D, k * 100.0D / i));
    return arraylist;
  }
  
  public void endStartSection(String name) {
    if (this.profilerLocalEnabled) {
      endSection();
      startSection(name);
    } 
  }
  
  public String getNameOfLastSection() {
    return (this.sectionList.size() == 0) ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
  }
  
  public static final class Profiler {}
}
