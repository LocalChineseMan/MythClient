package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.modules.display.HUD;

public class LayerCape implements LayerRenderer {
  private final RenderPlayer playerRenderer;
  
  private static final String __OBFID = "CL_00002425";
  
  public LayerCape(RenderPlayer playerRendererIn) {
    this.playerRenderer = playerRendererIn;
  }
  
  public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().getSession().getUsername())) {
      HUD hud = (HUD)Client.INSTANCE.moduleManager.getModuleByClass(HUD.class);
      if (hud.cape.is("None"))
        return; 
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.playerRenderer.bindTexture(hud.cape.is("None") ? entitylivingbaseIn.getLocationCape() : new ResourceLocation(getCape()));
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.125F);
      double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks - entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks;
      double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks - entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks;
      double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks - entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks;
      float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
      double d3 = MathHelper.sin(f * 3.1415927F / 180.0F);
      double d4 = -MathHelper.cos(f * 3.1415927F / 180.0F);
      float f1 = (float)d1 * 10.0F;
      f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
      float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      if (f2 < 0.0F)
        f2 = 0.0F; 
      if (f2 > 165.0F)
        f2 = 165.0F; 
      float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
      f1 += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;
      if (entitylivingbaseIn.isSneaking()) {
        f1 += 25.0F;
        GlStateManager.translate(0.0F, 0.142F, -0.0178F);
      } 
      GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      this.playerRenderer.getMainModel().renderCape(0.0625F);
      GlStateManager.popMatrix();
    } 
  }
  
  public String getCape() {
    HUD hud = (HUD)Client.INSTANCE.moduleManager.getModuleByClass(HUD.class);
    if (hud.cape.is("Hentai"))
      return "myth/Capes/hentai.png"; 
    if (hud.cape.is("Hentai 2"))
      return "myth/Capes/hentai2.png"; 
    if (hud.cape.is("Hentai 3"))
      return "myth/Capes/hentai3.png"; 
    return null;
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
  
  public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    doRenderLayer((AbstractClientPlayer)entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
  }
}
