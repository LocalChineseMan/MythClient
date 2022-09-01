package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.ComponentLike;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextReplacementConfig;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.ComponentFlattener;
import com.viaversion.viaversion.libs.kyori.adventure.text.flattener.FlattenerListener;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.NamedTextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextFormat;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import com.viaversion.viaversion.libs.kyori.adventure.util.Services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LegacyComponentSerializerImpl implements LegacyComponentSerializer {
  static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
  
  static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
  
  private static final TextDecoration[] DECORATIONS = TextDecoration.values();
  
  private static final char LEGACY_BUNGEE_HEX_CHAR = 'x';
  
  private static final List<TextFormat> FORMATS;
  
  private static final String LEGACY_CHARS;
  
  static {
    Map<TextFormat, String> formats = new LinkedHashMap<>(22);
    formats.put(NamedTextColor.BLACK, "0");
    formats.put(NamedTextColor.DARK_BLUE, "1");
    formats.put(NamedTextColor.DARK_GREEN, "2");
    formats.put(NamedTextColor.DARK_AQUA, "3");
    formats.put(NamedTextColor.DARK_RED, "4");
    formats.put(NamedTextColor.DARK_PURPLE, "5");
    formats.put(NamedTextColor.GOLD, "6");
    formats.put(NamedTextColor.GRAY, "7");
    formats.put(NamedTextColor.DARK_GRAY, "8");
    formats.put(NamedTextColor.BLUE, "9");
    formats.put(NamedTextColor.GREEN, "a");
    formats.put(NamedTextColor.AQUA, "b");
    formats.put(NamedTextColor.RED, "c");
    formats.put(NamedTextColor.LIGHT_PURPLE, "d");
    formats.put(NamedTextColor.YELLOW, "e");
    formats.put(NamedTextColor.WHITE, "f");
    formats.put(TextDecoration.OBFUSCATED, "k");
    formats.put(TextDecoration.BOLD, "l");
    formats.put(TextDecoration.STRIKETHROUGH, "m");
    formats.put(TextDecoration.UNDERLINED, "n");
    formats.put(TextDecoration.ITALIC, "o");
    formats.put(Reset.INSTANCE, "r");
    FORMATS = Collections.unmodifiableList(new ArrayList<>(formats.keySet()));
    LEGACY_CHARS = String.join("", formats.values());
    if (FORMATS.size() != LEGACY_CHARS.length())
      throw new IllegalStateException("FORMATS length differs from LEGACY_CHARS length"); 
  }
  
  private static final Optional<LegacyComponentSerializer.Provider> SERVICE = Services.service(LegacyComponentSerializer.Provider.class);
  
  static final Consumer<LegacyComponentSerializer.Builder> BUILDER = SERVICE
    .<Consumer<LegacyComponentSerializer.Builder>>map(LegacyComponentSerializer.Provider::legacy)
    .orElseGet(() -> ());
  
  private final char character;
  
  private final char hexCharacter;
  
  @Nullable
  private final TextReplacementConfig urlReplacementConfig;
  
  private final boolean hexColours;
  
  private final boolean useTerriblyStupidHexFormat;
  
  private final ComponentFlattener flattener;
  
  static final class Instances {
    static final LegacyComponentSerializer SECTION = LegacyComponentSerializerImpl.SERVICE
      .map(LegacyComponentSerializer.Provider::legacySection)
      .orElseGet(() -> new LegacyComponentSerializerImpl('§', '#', null, false, false, ComponentFlattener.basic()));
    
    static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializerImpl.SERVICE
      .map(LegacyComponentSerializer.Provider::legacyAmpersand)
      .orElseGet(() -> new LegacyComponentSerializerImpl('&', '#', null, false, false, ComponentFlattener.basic()));
  }
  
  LegacyComponentSerializerImpl(char character, char hexCharacter, @Nullable TextReplacementConfig urlReplacementConfig, boolean hexColours, boolean useTerriblyStupidHexFormat, ComponentFlattener flattener) {
    this.character = character;
    this.hexCharacter = hexCharacter;
    this.urlReplacementConfig = urlReplacementConfig;
    this.hexColours = hexColours;
    this.useTerriblyStupidHexFormat = useTerriblyStupidHexFormat;
    this.flattener = flattener;
  }
  
  @Nullable
  private FormatCodeType determineFormatType(char legacy, String input, int pos) {
    if (pos >= 14) {
      int expectedCharacterPosition = pos - 14;
      int expectedIndicatorPosition = pos - 13;
      if (input.charAt(expectedCharacterPosition) == this.character && input.charAt(expectedIndicatorPosition) == 'x')
        return FormatCodeType.BUNGEECORD_UNUSUAL_HEX; 
    } 
    if (legacy == this.hexCharacter && input.length() - pos >= 6)
      return FormatCodeType.KYORI_HEX; 
    if (LEGACY_CHARS.indexOf(legacy) != -1)
      return FormatCodeType.MOJANG_LEGACY; 
    return null;
  }
  
  @Nullable
  static LegacyFormat legacyFormat(char character) {
    int index = LEGACY_CHARS.indexOf(character);
    if (index != -1) {
      TextFormat format = FORMATS.get(index);
      if (format instanceof NamedTextColor)
        return new LegacyFormat((NamedTextColor)format); 
      if (format instanceof TextDecoration)
        return new LegacyFormat((TextDecoration)format); 
      if (format instanceof Reset)
        return LegacyFormat.RESET; 
    } 
    return null;
  }
  
  @Nullable
  private DecodedFormat decodeTextFormat(char legacy, String input, int pos) {
    FormatCodeType foundFormat = determineFormatType(legacy, input, pos);
    if (foundFormat == null)
      return null; 
    if (foundFormat == FormatCodeType.KYORI_HEX) {
      TextColor parsed = tryParseHexColor(input.substring(pos, pos + 6));
      if (parsed != null)
        return new DecodedFormat(foundFormat, (TextFormat)parsed); 
    } else {
      if (foundFormat == FormatCodeType.MOJANG_LEGACY)
        return new DecodedFormat(foundFormat, FORMATS.get(LEGACY_CHARS.indexOf(legacy))); 
      if (foundFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
        StringBuilder foundHex = new StringBuilder(6);
        for (int i = pos - 1; i >= pos - 11; i -= 2)
          foundHex.append(input.charAt(i)); 
        TextColor parsed = tryParseHexColor(foundHex.reverse().toString());
        if (parsed != null)
          return new DecodedFormat(foundFormat, (TextFormat)parsed); 
      } 
    } 
    return null;
  }
  
  @Nullable
  private static TextColor tryParseHexColor(String hexDigits) {
    try {
      int color = Integer.parseInt(hexDigits, 16);
      return TextColor.color(color);
    } catch (NumberFormatException ex) {
      return null;
    } 
  }
  
  private static boolean isHexTextColor(TextFormat format) {
    return (format instanceof TextColor && !(format instanceof NamedTextColor));
  }
  
  private String toLegacyCode(TextFormat format) {
    NamedTextColor namedTextColor;
    if (isHexTextColor(format)) {
      TextColor color = (TextColor)format;
      if (this.hexColours) {
        String hex = String.format("%06x", new Object[] { Integer.valueOf(color.value()) });
        if (this.useTerriblyStupidHexFormat) {
          StringBuilder legacy = new StringBuilder(String.valueOf('x'));
          for (char character : hex.toCharArray())
            legacy.append(this.character).append(character); 
          return legacy.toString();
        } 
        return this.hexCharacter + hex;
      } 
      namedTextColor = NamedTextColor.nearestTo(color);
    } 
    int index = FORMATS.indexOf(namedTextColor);
    return Character.toString(LEGACY_CHARS.charAt(index));
  }
  
  private TextComponent extractUrl(TextComponent component) {
    if (this.urlReplacementConfig == null)
      return component; 
    Component newComponent = component.replaceText(this.urlReplacementConfig);
    if (newComponent instanceof TextComponent)
      return (TextComponent)newComponent; 
    return TextComponent.ofChildren(new ComponentLike[] { (ComponentLike)newComponent });
  }
  
  @NotNull
  public TextComponent deserialize(@NotNull String input) {
    int next = input.lastIndexOf(this.character, input.length() - 2);
    if (next == -1)
      return extractUrl(Component.text(input)); 
    List<TextComponent> parts = new ArrayList<>();
    TextComponent.Builder current = null;
    boolean reset = false;
    int pos = input.length();
    do {
      DecodedFormat decoded = decodeTextFormat(input.charAt(next + 1), input, next + 2);
      if (decoded != null) {
        int from = next + ((decoded.encodedFormat == FormatCodeType.KYORI_HEX) ? 8 : 2);
        if (from != pos) {
          if (current != null) {
            if (reset) {
              parts.add((TextComponent)current.build());
              reset = false;
              current = Component.text();
            } else {
              current = (TextComponent.Builder)Component.text().append((Component)current.build());
            } 
          } else {
            current = Component.text();
          } 
          current.content(input.substring(from, pos));
        } else if (current == null) {
          current = Component.text();
        } 
        if (!reset)
          reset = applyFormat(current, decoded.format); 
        if (decoded.encodedFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX)
          next -= 12; 
        pos = next;
      } 
      next = input.lastIndexOf(this.character, next - 1);
    } while (next != -1);
    if (current != null)
      parts.add((TextComponent)current.build()); 
    String remaining = (pos > 0) ? input.substring(0, pos) : "";
    if (parts.size() == 1 && remaining.isEmpty())
      return extractUrl(parts.get(0)); 
    Collections.reverse(parts);
    return extractUrl((TextComponent)((TextComponent.Builder)Component.text().content(remaining).append(parts)).build());
  }
  
  @NotNull
  public String serialize(@NotNull Component component) {
    Cereal state = new Cereal(this, null);
    this.flattener.flatten(component, (FlattenerListener)state);
    return state.toString();
  }
  
  private static boolean applyFormat(TextComponent.Builder builder, @NotNull TextFormat format) {
    if (format instanceof TextColor) {
      builder.colorIfAbsent((TextColor)format);
      return true;
    } 
    if (format instanceof TextDecoration) {
      builder.decoration((TextDecoration)format, TextDecoration.State.TRUE);
      return false;
    } 
    if (format instanceof Reset)
      return true; 
    throw new IllegalArgumentException(String.format("unknown format '%s'", new Object[] { format.getClass() }));
  }
  
  @NotNull
  public LegacyComponentSerializer.Builder toBuilder() {
    return (LegacyComponentSerializer.Builder)new BuilderImpl(this);
  }
  
  private enum Reset implements TextFormat {
    INSTANCE;
  }
  
  enum FormatCodeType {
    MOJANG_LEGACY, KYORI_HEX, BUNGEECORD_UNUSUAL_HEX;
  }
  
  static final class DecodedFormat {
    final LegacyComponentSerializerImpl.FormatCodeType encodedFormat;
    
    final TextFormat format;
    
    private DecodedFormat(LegacyComponentSerializerImpl.FormatCodeType encodedFormat, TextFormat format) {
      if (format == null)
        throw new IllegalStateException("No format found"); 
      this.encodedFormat = encodedFormat;
      this.format = format;
    }
  }
  
  private final class LegacyComponentSerializerImpl {}
  
  static final class LegacyComponentSerializerImpl {}
}
