package joptsimple;

abstract class OptionParserState {
  static OptionParserState noMoreOptions() {
    return (OptionParserState)new Object();
  }
  
  static OptionParserState moreOptions(final boolean posixlyCorrect) {
    return new OptionParserState() {
        protected void handleArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
          String candidate = arguments.next();
          try {
            if (ParserRules.isOptionTerminator(candidate)) {
              parser.noMoreOptions();
              return;
            } 
            if (ParserRules.isLongOptionToken(candidate)) {
              parser.handleLongOptionToken(candidate, arguments, detectedOptions);
              return;
            } 
            if (ParserRules.isShortOptionToken(candidate)) {
              parser.handleShortOptionToken(candidate, arguments, detectedOptions);
              return;
            } 
          } catch (UnrecognizedOptionException e) {
            if (!parser.doesAllowsUnrecognizedOptions())
              throw e; 
          } 
          if (posixlyCorrect)
            parser.noMoreOptions(); 
          parser.handleNonOptionArgument(candidate, arguments, detectedOptions);
        }
      };
  }
  
  protected abstract void handleArgument(OptionParser paramOptionParser, ArgumentList paramArgumentList, OptionSet paramOptionSet);
}
