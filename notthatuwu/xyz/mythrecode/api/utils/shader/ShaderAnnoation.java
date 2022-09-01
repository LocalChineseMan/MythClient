package notthatuwu.xyz.mythrecode.api.utils.shader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ShaderAnnoation {
  String fragName();
  
  ShaderRenderType renderType();
}
