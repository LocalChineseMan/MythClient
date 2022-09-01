package notthatuwu.xyz.mythrecode.api.module.file;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface IFile {
  void save(Gson paramGson);
  
  void load(Gson paramGson);
  
  void setFile(File paramFile);
  
  default void writeFile(String content, File file) {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  default String readFile(File file) {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = bufferedReader.readLine()) != null)
        builder.append(line); 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return builder.toString();
  }
}
