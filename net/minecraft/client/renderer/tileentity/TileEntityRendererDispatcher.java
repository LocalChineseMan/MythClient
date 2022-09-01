package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class TileEntityRendererDispatcher {
  private final Map<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> mapSpecialRenderers = Maps.newHashMap();
  
  public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
  
  private FontRenderer fontRenderer;
  
  public static double staticPlayerX;
  
  public static double staticPlayerY;
  
  public static double staticPlayerZ;
  
  public TextureManager renderEngine;
  
  public World worldObj;
  
  public Entity entity;
  
  public float entityYaw;
  
  public float entityPitch;
  
  public double entityX;
  
  public double entityY;
  
  public double entityZ;
  
  private TileEntityRendererDispatcher() {
    this.mapSpecialRenderers.put(TileEntitySign.class, new TileEntitySignRenderer());
    this.mapSpecialRenderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
    this.mapSpecialRenderers.put(TileEntityPiston.class, new TileEntityPistonRenderer());
    this.mapSpecialRenderers.put(TileEntityChest.class, new TileEntityChestRenderer());
    this.mapSpecialRenderers.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
    this.mapSpecialRenderers.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
    this.mapSpecialRenderers.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
    this.mapSpecialRenderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
    this.mapSpecialRenderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());
    this.mapSpecialRenderers.put(TileEntityBanner.class, new TileEntityBannerRenderer());
    for (TileEntitySpecialRenderer<?> tileentityspecialrenderer : this.mapSpecialRenderers.values())
      tileentityspecialrenderer.setRendererDispatcher(this); 
  }
  
  public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRendererByClass(Class<? extends TileEntity> teClass) {
    TileEntitySpecialRenderer<? extends TileEntity> tileentityspecialrenderer = this.mapSpecialRenderers.get(teClass);
    if (tileentityspecialrenderer == null && teClass != TileEntity.class) {
      tileentityspecialrenderer = getSpecialRendererByClass((Class)teClass.getSuperclass());
      this.mapSpecialRenderers.put(teClass, tileentityspecialrenderer);
    } 
    return (TileEntitySpecialRenderer)tileentityspecialrenderer;
  }
  
  public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRenderer(TileEntity tileEntityIn) {
    return (tileEntityIn == null) ? null : getSpecialRendererByClass((Class)tileEntityIn.getClass());
  }
  
  public void cacheActiveRenderInfo(World worldIn, TextureManager textureManagerIn, FontRenderer fontrendererIn, Entity entityIn, float partialTicks) {
    if (this.worldObj != worldIn)
      setWorld(worldIn); 
    this.renderEngine = textureManagerIn;
    this.entity = entityIn;
    this.fontRenderer = fontrendererIn;
    this.entityYaw = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
    this.entityPitch = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks;
    this.entityX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
    this.entityY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
    this.entityZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
  }
  
  public void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage) {
    if (tileentityIn.getDistanceSq(this.entityX, this.entityY, this.entityZ) < tileentityIn.getMaxRenderDistanceSquared()) {
      int i = this.worldObj.getCombinedLight(tileentityIn.getPos(), 0);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlockPos blockpos = tileentityIn.getPos();
      renderTileEntityAt(tileentityIn, blockpos.getX() - staticPlayerX, blockpos.getY() - staticPlayerY, blockpos.getZ() - staticPlayerZ, partialTicks, destroyStage);
    } 
  }
  
  public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks) {
    renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, -1);
  }
  
  public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
    TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = getSpecialRenderer(tileEntityIn);
    if (tileentityspecialrenderer != null)
      try {
        tileentityspecialrenderer.renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, destroyStage);
      } catch (Throwable throwable) {
        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
        CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
        tileEntityIn.addInfoToCrashReport(crashreportcategory);
        throw new ReportedException(crashreport);
      }  
  }
  
  public void setWorld(World worldIn) {
    this.worldObj = worldIn;
  }
  
  public FontRenderer getFontRenderer() {
    return this.fontRenderer;
  }
}
