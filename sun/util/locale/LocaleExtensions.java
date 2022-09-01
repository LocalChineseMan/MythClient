package sun.util.locale;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class LocaleExtensions {
  private final Map<Character, Extension> extensionMap;
  
  private final String id;
  
  public static final LocaleExtensions CALENDAR_JAPANESE = new LocaleExtensions("u-ca-japanese", 
      
      Character.valueOf('u'), UnicodeLocaleExtension.CA_JAPANESE);
  
  public static final LocaleExtensions NUMBER_THAI = new LocaleExtensions("u-nu-thai", 
      
      Character.valueOf('u'), UnicodeLocaleExtension.NU_THAI);
  
  private LocaleExtensions(String paramString, Character paramCharacter, Extension paramExtension) {
    this.id = paramString;
    this.extensionMap = Collections.singletonMap(paramCharacter, paramExtension);
  }
  
  LocaleExtensions(Map<InternalLocaleBuilder.CaseInsensitiveChar, String> paramMap, Set<InternalLocaleBuilder.CaseInsensitiveString> paramSet, Map<InternalLocaleBuilder.CaseInsensitiveString, String> paramMap1) {
    boolean bool1 = !LocaleUtils.isEmpty(paramMap) ? true : false;
    boolean bool2 = !LocaleUtils.isEmpty(paramSet) ? true : false;
    boolean bool3 = !LocaleUtils.isEmpty(paramMap1) ? true : false;
    if (!bool1 && !bool2 && !bool3) {
      this.id = "";
      this.extensionMap = Collections.emptyMap();
      return;
    } 
    TreeMap<Object, Object> treeMap = new TreeMap<>();
    if (bool1)
      for (Map.Entry<InternalLocaleBuilder.CaseInsensitiveChar, String> entry : paramMap.entrySet()) {
        char c = LocaleUtils.toLower(((InternalLocaleBuilder.CaseInsensitiveChar)entry.getKey()).value());
        String str = (String)entry.getValue();
        if (LanguageTag.isPrivateusePrefixChar(c)) {
          str = InternalLocaleBuilder.removePrivateuseVariant(str);
          if (str == null)
            continue; 
        } 
        treeMap.put(Character.valueOf(c), new Extension(c, LocaleUtils.toLowerString(str)));
      }  
    if (bool2 || bool3) {
      TreeSet<String> treeSet = null;
      TreeMap<Object, Object> treeMap1 = null;
      if (bool2) {
        treeSet = new TreeSet();
        for (InternalLocaleBuilder.CaseInsensitiveString caseInsensitiveString : paramSet)
          treeSet.add(LocaleUtils.toLowerString(caseInsensitiveString.value())); 
      } 
      if (bool3) {
        treeMap1 = new TreeMap<>();
        for (Map.Entry<InternalLocaleBuilder.CaseInsensitiveString, String> entry : paramMap1.entrySet()) {
          String str1 = LocaleUtils.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)entry.getKey()).value());
          String str2 = LocaleUtils.toLowerString((String)entry.getValue());
          treeMap1.put(str1, str2);
        } 
      } 
      UnicodeLocaleExtension unicodeLocaleExtension = new UnicodeLocaleExtension(treeSet, (SortedMap)treeMap1);
      treeMap.put(Character.valueOf('u'), unicodeLocaleExtension);
    } 
    if (treeMap.isEmpty()) {
      this.id = "";
      this.extensionMap = Collections.emptyMap();
    } else {
      this.id = toID((SortedMap)treeMap);
      this.extensionMap = (Map)treeMap;
    } 
  }
  
  public Set<Character> getKeys() {
    if (this.extensionMap.isEmpty())
      return Collections.emptySet(); 
    return Collections.unmodifiableSet(this.extensionMap.keySet());
  }
  
  public Extension getExtension(Character paramCharacter) {
    return this.extensionMap.get(Character.valueOf(LocaleUtils.toLower(paramCharacter.charValue())));
  }
  
  public String getExtensionValue(Character paramCharacter) {
    Extension extension = this.extensionMap.get(Character.valueOf(LocaleUtils.toLower(paramCharacter.charValue())));
    if (extension == null)
      return null; 
    return extension.getValue();
  }
  
  public Set<String> getUnicodeLocaleAttributes() {
    Extension extension = this.extensionMap.get(Character.valueOf('u'));
    if (extension == null)
      return Collections.emptySet(); 
    assert extension instanceof UnicodeLocaleExtension;
    return ((UnicodeLocaleExtension)extension).getUnicodeLocaleAttributes();
  }
  
  public Set<String> getUnicodeLocaleKeys() {
    Extension extension = this.extensionMap.get(Character.valueOf('u'));
    if (extension == null)
      return Collections.emptySet(); 
    assert extension instanceof UnicodeLocaleExtension;
    return ((UnicodeLocaleExtension)extension).getUnicodeLocaleKeys();
  }
  
  public String getUnicodeLocaleType(String paramString) {
    Extension extension = this.extensionMap.get(Character.valueOf('u'));
    if (extension == null)
      return null; 
    assert extension instanceof UnicodeLocaleExtension;
    return ((UnicodeLocaleExtension)extension).getUnicodeLocaleType(LocaleUtils.toLowerString(paramString));
  }
  
  public boolean isEmpty() {
    return this.extensionMap.isEmpty();
  }
  
  public static boolean isValidKey(char paramChar) {
    return (LanguageTag.isExtensionSingletonChar(paramChar) || LanguageTag.isPrivateusePrefixChar(paramChar));
  }
  
  public static boolean isValidUnicodeLocaleKey(String paramString) {
    return UnicodeLocaleExtension.isKey(paramString);
  }
  
  private static String toID(SortedMap<Character, Extension> paramSortedMap) {
    StringBuilder stringBuilder = new StringBuilder();
    Extension extension = null;
    for (Map.Entry<Character, Extension> entry : paramSortedMap.entrySet()) {
      char c = ((Character)entry.getKey()).charValue();
      Extension extension1 = (Extension)entry.getValue();
      if (LanguageTag.isPrivateusePrefixChar(c)) {
        extension = extension1;
        continue;
      } 
      if (stringBuilder.length() > 0)
        stringBuilder.append("-"); 
      stringBuilder.append(extension1);
    } 
    if (extension != null) {
      if (stringBuilder.length() > 0)
        stringBuilder.append("-"); 
      stringBuilder.append(extension);
    } 
    return stringBuilder.toString();
  }
  
  public String toString() {
    return this.id;
  }
  
  public String getID() {
    return this.id;
  }
  
  public int hashCode() {
    return this.id.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof LocaleExtensions))
      return false; 
    return this.id.equals(((LocaleExtensions)paramObject).id);
  }
}
