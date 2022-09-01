package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
  private static final Splitter splitter = Splitter.on('=').limit(2);
  
  private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
  
  Map<String, String> properties = Maps.newHashMap();
  
  private boolean unicode;
  
  public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> p_135022_2_) {
    this.properties.clear();
    for (String s : p_135022_2_) {
      String s1 = String.format("lang/%s.lang", new Object[] { s });
      for (String s2 : resourceManager.getResourceDomains()) {
        try {
          loadLocaleData(resourceManager.getAllResources(new ResourceLocation(s2, s1)));
        } catch (IOException iOException) {}
      } 
    } 
    checkUnicode();
  }
  
  public boolean isUnicode() {
    return this.unicode;
  }
  
  private void checkUnicode() {
    this.unicode = false;
    int i = 0;
    int j = 0;
    for (String s : this.properties.values()) {
      int k = s.length();
      j += k;
      for (int l = 0; l < k; l++) {
        if (s.charAt(l) >= 'Ā')
          i++; 
      } 
    } 
    float f = i / j;
    this.unicode = (f > 0.1D);
  }
  
  private void loadLocaleData(List<IResource> p_135028_1_) throws IOException {
    for (IResource iresource : p_135028_1_) {
      InputStream inputstream = iresource.getInputStream();
      try {
        loadLocaleData(inputstream);
      } finally {
        IOUtils.closeQuietly(inputstream);
      } 
    } 
  }
  
  private void loadLocaleData(InputStream p_135021_1_) throws IOException {
    for (String s : IOUtils.readLines(p_135021_1_, Charsets.UTF_8)) {
      if (!s.isEmpty() && s.charAt(0) != '#') {
        String[] astring = (String[])Iterables.toArray(splitter.split(s), String.class);
        if (astring != null && astring.length == 2) {
          String s1 = astring[0];
          String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
          this.properties.put(s1, s2);
        } 
      } 
    } 
  }
  
  private String translateKeyPrivate(String p_135026_1_) {
    String s = this.properties.get(p_135026_1_);
    return (s == null) ? p_135026_1_ : s;
  }
  
  public String formatMessage(String translateKey, Object[] parameters) {
    String s = translateKeyPrivate(translateKey);
    try {
      return String.format(s, parameters);
    } catch (IllegalFormatException var5) {
      return "Format error: " + s;
    } 
  }
}
