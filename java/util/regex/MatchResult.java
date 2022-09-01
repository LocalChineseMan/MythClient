package java.util.regex;

public interface MatchResult {
  int start();
  
  int start(int paramInt);
  
  int end();
  
  int end(int paramInt);
  
  String group();
  
  String group(int paramInt);
  
  int groupCount();
}
