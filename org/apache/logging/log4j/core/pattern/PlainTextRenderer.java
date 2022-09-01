package org.apache.logging.log4j.core.pattern;

public final class PlainTextRenderer implements TextRenderer {
  private static final PlainTextRenderer INSTANCE = new PlainTextRenderer();
  
  public static PlainTextRenderer getInstance() {
    return INSTANCE;
  }
  
  public void render(String input, StringBuilder output, String styleName) {
    output.append(input);
  }
  
  public void render(StringBuilder input, StringBuilder output) {
    output.append(input);
  }
}
