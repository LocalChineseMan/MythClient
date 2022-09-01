package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomItems;
import optifine.Reflector;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
  protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
  
  protected ModelBase field_177189_c;
  
  protected ModelBase field_177186_d;
  
  private final RendererLivingEntity renderer;
  
  private final float alpha = 1.0F;
  
  private final float colorR = 1.0F;
  
  private final float colorG = 1.0F;
  
  private final float colorB = 1.0F;
  
  private boolean field_177193_i;
  
  private static final Map ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
  
  private static final String __OBFID = "CL_00002428";
  
  public LayerArmorBase(RendererLivingEntity rendererIn) {
    this.renderer = rendererIn;
    initArmor();
  }
  
  public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4);
    renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3);
    renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2);
    renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1);
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
  
  private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float p_177182_4_, float p_177182_5_, float p_177182_6_, float p_177182_7_, float p_177182_8_, int armorSlot) {
    ItemStack itemstack = getCurrentArmor(entitylivingbaseIn, armorSlot);
    if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
      int i;
      float f, f1, f2;
      ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
      ModelBase modelbase = func_177175_a(armorSlot);
      modelbase.setModelAttributes(this.renderer.getMainModel());
      modelbase.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_4_);
      if (Reflector.ForgeHooksClient.exists())
        modelbase = getArmorModelHook(entitylivingbaseIn, itemstack, armorSlot, modelbase); 
      func_177179_a((T)modelbase, armorSlot);
      boolean flag = isSlotForLeggings(armorSlot);
      if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, null))
        if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
          this.renderer.bindTexture(getArmorResource((Entity)entitylivingbaseIn, itemstack, flag ? 2 : 1, null));
        } else {
          this.renderer.bindTexture(getArmorResource(itemarmor, flag));
        }  
      if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
        int j = itemarmor.getColor(itemstack);
        if (j != -1) {
          float f3 = (j >> 16 & 0xFF) / 255.0F;
          float f4 = (j >> 8 & 0xFF) / 255.0F;
          float f5 = (j & 0xFF) / 255.0F;
          getClass();
          getClass();
          getClass();
          getClass();
          GlStateManager.color(1.0F * f3, 1.0F * f4, 1.0F * f5, 1.0F);
          modelbase.render((Entity)entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
          if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, "overlay"))
            this.renderer.bindTexture(getArmorResource((Entity)entitylivingbaseIn, itemstack, flag ? 2 : 1, "overlay")); 
        } 
        getClass();
        getClass();
        getClass();
        getClass();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        modelbase.render((Entity)entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
        if (!this.field_177193_i && itemstack.isItemEnchanted() && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemstack, modelbase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_)))
          func_177183_a(entitylivingbaseIn, modelbase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_); 
        return;
      } 
      switch (LayerArmorBase$1.field_178747_a[itemarmor.getArmorMaterial().ordinal()]) {
        case 1:
          i = itemarmor.getColor(itemstack);
          f = (i >> 16 & 0xFF) / 255.0F;
          f1 = (i >> 8 & 0xFF) / 255.0F;
          f2 = (i & 0xFF) / 255.0F;
          getClass();
          getClass();
          getClass();
          getClass();
          GlStateManager.color(1.0F * f, 1.0F * f1, 1.0F * f2, 1.0F);
          modelbase.render((Entity)entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
          if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, "overlay"))
            this.renderer.bindTexture(getArmorResource(itemarmor, flag, "overlay")); 
        case 2:
        case 3:
        case 4:
        case 5:
          getClass();
          getClass();
          getClass();
          getClass();
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          modelbase.render((Entity)entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
          break;
      } 
      if (!this.field_177193_i && itemstack.isItemEnchanted() && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemstack, modelbase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_)))
        func_177183_a(entitylivingbaseIn, modelbase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_); 
    } 
  }
  
  public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot) {
    return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
  }
  
  public ModelBase func_177175_a(int p_177175_1_) {
    return isSlotForLeggings(p_177175_1_) ? this.field_177189_c : this.field_177186_d;
  }
  
  private boolean isSlotForLeggings(int armorSlot) {
    return (armorSlot == 2);
  }
  
  private void func_177183_a(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
    if (!Config.isCustomItems() || CustomItems.isUseGlint())
      if (!Config.isShaders() || !Shaders.isShadowPass) {
        float f = entitylivingbaseIn.ticksExisted + p_177183_5_;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        if (Config.isShaders())
          ShadersRender.renderEnchantedGlintBegin(); 
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(f1, f1, f1, 1.0F);
        for (int i = 0; i < 2; i++) {
          GlStateManager.disableLighting();
          GlStateManager.blendFunc(768, 1);
          float f2 = 0.76F;
          GlStateManager.color(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
          GlStateManager.matrixMode(5890);
          GlStateManager.loadIdentity();
          float f3 = 0.33333334F;
          GlStateManager.scale(f3, f3, f3);
          GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.translate(0.0F, f * (0.001F + i * 0.003F) * 20.0F, 0.0F);
          GlStateManager.matrixMode(5888);
          modelbaseIn.render((Entity)entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
        } 
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        if (Config.isShaders())
          ShadersRender.renderEnchantedGlintEnd(); 
      }  
  }
  
  private ResourceLocation getArmorResource(ItemArmor p_177181_1_, boolean p_177181_2_) {
    return getArmorResource(p_177181_1_, p_177181_2_, null);
  }
  
  private ResourceLocation getArmorResource(ItemArmor p_177178_1_, boolean p_177178_2_, String p_177178_3_) {
    String s = String.format("textures/models/armor/%s_layer_%d%s.png", new Object[] { p_177178_1_.getArmorMaterial().getName(), Integer.valueOf(p_177178_2_ ? 2 : 1), (p_177178_3_ == null) ? "" : String.format("_%s", new Object[] { p_177178_3_ }) });
    ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s);
    if (resourcelocation == null) {
      resourcelocation = new ResourceLocation(s);
      ARMOR_TEXTURE_RES_MAP.put(s, resourcelocation);
    } 
    return resourcelocation;
  }
  
  protected abstract void initArmor();
  
  protected abstract void func_177179_a(T paramT, int paramInt);
  
  protected ModelBase getArmorModelHook(EntityLivingBase p_getArmorModelHook_1_, ItemStack p_getArmorModelHook_2_, int p_getArmorModelHook_3_, ModelBase p_getArmorModelHook_4_) {
    return p_getArmorModelHook_4_;
  }
  
  public ResourceLocation getArmorResource(Entity p_getArmorResource_1_, ItemStack p_getArmorResource_2_, int p_getArmorResource_3_, String p_getArmorResource_4_) {
    ItemArmor itemarmor = (ItemArmor)p_getArmorResource_2_.getItem();
    String s = itemarmor.getArmorMaterial().getName();
    String s1 = "minecraft";
    int i = s.indexOf(':');
    if (i != -1) {
      s1 = s.substring(0, i);
      s = s.substring(i + 1);
    } 
    String s2 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", new Object[] { s1, s, Integer.valueOf((p_getArmorResource_3_ == 2) ? 2 : 1), (p_getArmorResource_4_ == null) ? "" : String.format("_%s", new Object[] { p_getArmorResource_4_ }) });
    s2 = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, new Object[] { p_getArmorResource_1_, p_getArmorResource_2_, s2, Integer.valueOf(p_getArmorResource_3_), p_getArmorResource_4_ });
    ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s2);
    if (resourcelocation == null) {
      resourcelocation = new ResourceLocation(s2);
      ARMOR_TEXTURE_RES_MAP.put(s2, resourcelocation);
    } 
    return resourcelocation;
  }
  
  static final class LayerArmorBase$1 {
    static final int[] field_178747_a = new int[(ItemArmor.ArmorMaterial.values()).length];
    
    private static final String __OBFID = "CL_00002427";
    
    static {
      try {
        field_178747_a[ItemArmor.ArmorMaterial.LEATHER.ordinal()] = 1;
      } catch (NoSuchFieldError noSuchFieldError) {}
      try {
        field_178747_a[ItemArmor.ArmorMaterial.CHAIN.ordinal()] = 2;
      } catch (NoSuchFieldError noSuchFieldError) {}
      try {
        field_178747_a[ItemArmor.ArmorMaterial.IRON.ordinal()] = 3;
      } catch (NoSuchFieldError noSuchFieldError) {}
      try {
        field_178747_a[ItemArmor.ArmorMaterial.GOLD.ordinal()] = 4;
      } catch (NoSuchFieldError noSuchFieldError) {}
      try {
        field_178747_a[ItemArmor.ArmorMaterial.DIAMOND.ordinal()] = 5;
      } catch (NoSuchFieldError noSuchFieldError) {}
    }
  }
}
