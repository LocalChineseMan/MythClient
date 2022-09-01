package net.minecraft.client.renderer;

public class Tessellator {
  private final WorldRenderer worldRenderer;
  
  private final WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
  
  public static final Tessellator instance = new Tessellator(2097152);
  
  public static Tessellator getInstance() {
    return instance;
  }
  
  public Tessellator(int bufferSize) {
    this.worldRenderer = new WorldRenderer(bufferSize);
  }
  
  public void draw() {
    this.worldRenderer.finishDrawing();
    this.vboUploader.func_181679_a(this.worldRenderer);
  }
  
  public WorldRenderer getWorldRenderer() {
    return this.worldRenderer;
  }
}
