package joptsimple;

import java.util.Collection;

class RequiredArgumentOptionSpec<V> extends ArgumentAcceptingOptionSpec<V> {
  RequiredArgumentOptionSpec(String option) {
    super(option, true);
  }
  
  RequiredArgumentOptionSpec(Collection<String> options, String description) {
    super(options, true, description);
  }
  
  protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
    if (!arguments.hasMore())
      throw new OptionMissingRequiredArgumentException(options()); 
    addArguments(detectedOptions, arguments.next());
  }
}
