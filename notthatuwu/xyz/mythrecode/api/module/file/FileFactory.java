package notthatuwu.xyz.mythrecode.api.module.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import net.minecraft.client.Minecraft;

public class FileFactory extends Container<IFile> {
  private final Gson GSON = (new GsonBuilder()).setPrettyPrinting().serializeNulls().create();
  
  public File cape;
  
  private File root;
  
  public void add(IFile item) {
    item.setFile(this.root);
    super.add(item);
  }
  
  public void saveFile(Class<? extends IFile> iFile) {
    IFile file = findByClass(iFile);
    if (file != null)
      file.save(this.GSON); 
  }
  
  public void loadFile(Class<? extends IFile> iFile) {
    IFile file = findByClass(iFile);
    if (file != null)
      file.load(this.GSON); 
  }
  
  public void save() {
    forEach(file -> file.save(this.GSON));
  }
  
  public void load() {
    forEach(file -> file.load(this.GSON));
  }
  
  public void setupRoot(String name) {
    this.cape = new File((Minecraft.getMinecraft()).mcDataDir, "Myth/Capes");
    if (!this.cape.exists())
      this.cape.mkdir(); 
    this.root = new File((Minecraft.getMinecraft()).mcDataDir, name);
    if (!this.root.exists() && 
      !this.root.mkdirs())
      Minecraft.logger.warn("Failed to create the root folder \"" + this.root.getPath() + "\"."); 
  }
}
