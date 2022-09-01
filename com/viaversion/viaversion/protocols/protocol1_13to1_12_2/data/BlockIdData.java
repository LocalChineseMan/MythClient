package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.ObjectArrays;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BlockIdData {
  public static final String[] PREVIOUS = new String[0];
  
  public static Map<String, String[]> blockIdMapping;
  
  public static Map<String, String[]> fallbackReverseMapping;
  
  public static Int2ObjectMap<String> numberIdToString;
  
  public static void init() {
    InputStream stream = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockIds1.12to1.13.json");
    try {
      InputStreamReader reader = new InputStreamReader(stream);
      try {
        Map<String, String[]> map = (Map<String, String[]>)GsonUtil.getGson().fromJson(reader, (new TypeToken<Map<String, String[]>>() {
            
            }).getType());
        blockIdMapping = (Map)new HashMap<>((Map)map);
        fallbackReverseMapping = (Map)new HashMap<>();
        for (Map.Entry<String, String[]> entry : blockIdMapping.entrySet()) {
          for (String val : (String[])entry.getValue()) {
            String[] previous = fallbackReverseMapping.get(val);
            if (previous == null)
              previous = PREVIOUS; 
            fallbackReverseMapping.put(val, (String[])ObjectArrays.concat((Object[])previous, entry.getKey()));
          } 
        } 
        reader.close();
      } catch (Throwable throwable) {
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    InputStream blockS = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockNumberToString1.12.json");
    try {
      InputStreamReader blockR = new InputStreamReader(blockS);
      try {
        Map<Integer, String> map = (Map<Integer, String>)GsonUtil.getGson().fromJson(blockR, (new TypeToken<Map<Integer, String>>() {
            
            }).getType());
        numberIdToString = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap(map);
        blockR.close();
      } catch (Throwable throwable) {
        try {
          blockR.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
