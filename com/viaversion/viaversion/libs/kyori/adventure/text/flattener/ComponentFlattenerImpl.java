package com.viaversion.viaversion.libs.kyori.adventure.text.flattener;

import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.KeybindComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.ScoreComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.SelectorComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TextComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.TranslatableComponent;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.Style;
import com.viaversion.viaversion.libs.kyori.adventure.util.Buildable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentFlattenerImpl implements ComponentFlattener {
  static final ComponentFlattener BASIC;
  
  static {
    BASIC = (ComponentFlattener)(new BuilderImpl()).<KeybindComponent>mapper(KeybindComponent.class, component -> component.keybind()).<ScoreComponent>mapper(ScoreComponent.class, ScoreComponent::value).<SelectorComponent>mapper(SelectorComponent.class, SelectorComponent::pattern).<TextComponent>mapper(TextComponent.class, TextComponent::content).<TranslatableComponent>mapper(TranslatableComponent.class, TranslatableComponent::key).build();
  }
  
  static final ComponentFlattener TEXT_ONLY = (ComponentFlattener)(new BuilderImpl())
    .<TextComponent>mapper(TextComponent.class, TextComponent::content)
    .build();
  
  private static final int MAX_DEPTH = 512;
  
  private final Map<Class<?>, Function<?, String>> flatteners;
  
  private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
  
  private final ConcurrentMap<Class<?>, Handler> propagatedFlatteners = new ConcurrentHashMap<>();
  
  private final Function<Component, String> unknownHandler;
  
  ComponentFlattenerImpl(Map<Class<?>, Function<?, String>> flatteners, Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, @Nullable Function<Component, String> unknownHandler) {
    this.flatteners = Collections.unmodifiableMap(new HashMap<>(flatteners));
    this.complexFlatteners = Collections.unmodifiableMap(new HashMap<>(complexFlatteners));
    this.unknownHandler = unknownHandler;
  }
  
  public void flatten(@NotNull Component input, @NotNull FlattenerListener listener) {
    flatten0(input, listener, 0);
  }
  
  private void flatten0(@NotNull Component input, @NotNull FlattenerListener listener, int depth) {
    Objects.requireNonNull(input, "input");
    Objects.requireNonNull(listener, "listener");
    if (input == Component.empty())
      return; 
    if (depth > 512)
      throw new IllegalStateException("Exceeded maximum depth of 512 while attempting to flatten components!"); 
    Handler flattener = flattener(input);
    Style inputStyle = input.style();
    listener.pushStyle(inputStyle);
    try {
      if (flattener != null)
        flattener.handle(input, listener, depth + 1); 
      if (!input.children().isEmpty())
        for (Component child : input.children())
          flatten0(child, listener, depth + 1);  
    } finally {
      listener.popStyle(inputStyle);
    } 
  }
  
  @Nullable
  private <T extends Component> Handler flattener(T test) {
    Handler flattener = this.propagatedFlatteners.computeIfAbsent(test.getClass(), key -> {
          Function<Component, String> value = (Function<Component, String>)this.flatteners.get(key);
          if (value != null)
            return (); 
          for (Map.Entry<Class<?>, Function<?, String>> entry : this.flatteners.entrySet()) {
            if (((Class)entry.getKey()).isAssignableFrom(key))
              return (); 
          } 
          BiConsumer<Component, Consumer<Component>> complexValue = (BiConsumer<Component, Consumer<Component>>)this.complexFlatteners.get(key);
          if (complexValue != null)
            return (); 
          for (Map.Entry<Class<?>, BiConsumer<?, Consumer<Component>>> entry : this.complexFlatteners.entrySet()) {
            if (((Class)entry.getKey()).isAssignableFrom(key))
              return (); 
          } 
          return Handler.NONE;
        });
    if (flattener == Handler.NONE)
      return (this.unknownHandler == null) ? null : ((component, listener, depth) -> this.unknownHandler.apply(component)); 
    return flattener;
  }
  
  public ComponentFlattener.Builder toBuilder() {
    return new BuilderImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
  }
  
  static interface ComponentFlattenerImpl {}
  
  static final class BuilderImpl implements ComponentFlattener.Builder {
    private final Map<Class<?>, Function<?, String>> flatteners;
    
    private final Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners;
    
    @Nullable
    private Function<Component, String> unknownHandler;
    
    BuilderImpl() {
      this.flatteners = new HashMap<>();
      this.complexFlatteners = new HashMap<>();
    }
    
    BuilderImpl(Map<Class<?>, Function<?, String>> flatteners, Map<Class<?>, BiConsumer<?, Consumer<Component>>> complexFlatteners, @Nullable Function<Component, String> unknownHandler) {
      this.flatteners = new HashMap<>(flatteners);
      this.complexFlatteners = new HashMap<>(complexFlatteners);
      this.unknownHandler = unknownHandler;
    }
    
    @NotNull
    public ComponentFlattener build() {
      return (ComponentFlattener)new ComponentFlattenerImpl(this.flatteners, this.complexFlatteners, this.unknownHandler);
    }
    
    public <T extends Component> ComponentFlattener.Builder mapper(@NotNull Class<T> type, @NotNull Function<T, String> converter) {
      validateNoneInHierarchy((Class<? extends Component>)Objects.requireNonNull(type, "type"));
      this.flatteners.put(type, 
          
          Objects.<Function<?, String>>requireNonNull(converter, "converter"));
      this.complexFlatteners.remove(type);
      return this;
    }
    
    public <T extends Component> ComponentFlattener.Builder complexMapper(@NotNull Class<T> type, @NotNull BiConsumer<T, Consumer<Component>> converter) {
      validateNoneInHierarchy((Class<? extends Component>)Objects.requireNonNull(type, "type"));
      this.complexFlatteners.put(type, 
          
          Objects.<BiConsumer<?, Consumer<Component>>>requireNonNull(converter, "converter"));
      this.flatteners.remove(type);
      return this;
    }
    
    private void validateNoneInHierarchy(Class<? extends Component> beingRegistered) {
      for (Class<?> clazz : this.flatteners.keySet())
        testHierarchy(clazz, beingRegistered); 
      for (Class<?> clazz : this.complexFlatteners.keySet())
        testHierarchy(clazz, beingRegistered); 
    }
    
    private static void testHierarchy(Class<?> existing, Class<?> beingRegistered) {
      if (!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing)))
        throw new IllegalArgumentException("Conflict detected between already registered type " + existing + " and newly registered type " + beingRegistered + "! Types in a component flattener must not share a common hierachy!"); 
    }
    
    public ComponentFlattener.Builder unknownMapper(@Nullable Function<Component, String> converter) {
      this.unknownHandler = converter;
      return this;
    }
  }
}
