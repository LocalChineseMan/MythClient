package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.ComponentFlattener;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.ComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LegacyComponentSerializer extends ComponentSerializer<Component, TextComponent, String>, Buildable<LegacyComponentSerializer, LegacyComponentSerializer.Builder> {
  public static final char SECTION_CHAR = '§';
  
  public static final char AMPERSAND_CHAR = '&';
  
  public static final char HEX_CHAR = '#';
  
  @NotNull
  static LegacyComponentSerializer legacySection() {
    return LegacyComponentSerializerImpl.Instances.SECTION;
  }
  
  @NotNull
  static LegacyComponentSerializer legacyAmpersand() {
    return LegacyComponentSerializerImpl.Instances.AMPERSAND;
  }
  
  @NotNull
  static LegacyComponentSerializer legacy(char legacyCharacter) {
    if (legacyCharacter == '§')
      return legacySection(); 
    if (legacyCharacter == '&')
      return legacyAmpersand(); 
    return builder().character(legacyCharacter).build();
  }
  
  @Nullable
  static LegacyFormat parseChar(char character) {
    return LegacyComponentSerializerImpl.legacyFormat(character);
  }
  
  @NotNull
  static Builder builder() {
    return (Builder)new LegacyComponentSerializerImpl.BuilderImpl();
  }
  
  @NotNull
  TextComponent deserialize(@NotNull String paramString);
  
  @NotNull
  String serialize(@NotNull Component paramComponent);
  
  public static interface Builder extends Buildable.Builder<LegacyComponentSerializer> {
    @NotNull
    Builder character(char param1Char);
    
    @NotNull
    Builder hexCharacter(char param1Char);
    
    @NotNull
    Builder extractUrls();
    
    @NotNull
    Builder extractUrls(@NotNull Pattern param1Pattern);
    
    @NotNull
    Builder extractUrls(@Nullable Style param1Style);
    
    @NotNull
    Builder extractUrls(@NotNull Pattern param1Pattern, @Nullable Style param1Style);
    
    @NotNull
    Builder hexColors();
    
    @NotNull
    Builder useUnusualXRepeatedCharacterHexFormat();
    
    @NotNull
    Builder flattener(@NotNull ComponentFlattener param1ComponentFlattener);
    
    @NotNull
    LegacyComponentSerializer build();
  }
  
  @Internal
  public static interface Provider {
    @Internal
    @NotNull
    LegacyComponentSerializer legacyAmpersand();
    
    @Internal
    @NotNull
    LegacyComponentSerializer legacySection();
    
    @Internal
    @NotNull
    Consumer<LegacyComponentSerializer.Builder> legacy();
  }
}
