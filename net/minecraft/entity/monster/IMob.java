package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals {
  public static final Predicate<Entity> mobSelector = (Predicate<Entity>)new Object();
  
  public static final Predicate<Entity> VISIBLE_MOB_SELECTOR = (Predicate<Entity>)new Object();
}
