package com.viaversion.viaversion.api.minecraft.entities;

import com.viaversion.viaversion.api.Via;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Entity1_13Types {
  public static EntityType getTypeFromId(int typeID, boolean isObject) {
    Optional<EntityType> type;
    if (isObject) {
      type = ObjectType.getPCEntity(typeID);
    } else {
      type = EntityType.findById(typeID);
    } 
    if (!type.isPresent()) {
      Via.getPlatform().getLogger().severe("Could not find 1.13 type id " + typeID + " isObject=" + isObject);
      return EntityType.ENTITY;
    } 
    return type.get();
  }
  
  public enum EntityType implements EntityType {
    ENTITY(-1),
    AREA_EFFECT_CLOUD(0, ENTITY),
    END_CRYSTAL(16, ENTITY),
    EVOKER_FANGS(20, ENTITY),
    EXPERIENCE_ORB(22, ENTITY),
    EYE_OF_ENDER(23, ENTITY),
    FALLING_BLOCK(24, ENTITY),
    FIREWORK_ROCKET(25, ENTITY),
    ITEM(32, ENTITY),
    LLAMA_SPIT(37, ENTITY),
    TNT(55, ENTITY),
    SHULKER_BULLET(60, ENTITY),
    FISHING_BOBBER(93, ENTITY),
    LIVINGENTITY(-1, ENTITY),
    ARMOR_STAND(1, LIVINGENTITY),
    PLAYER(92, LIVINGENTITY),
    ABSTRACT_INSENTIENT(-1, LIVINGENTITY),
    ENDER_DRAGON(17, ABSTRACT_INSENTIENT),
    ABSTRACT_CREATURE(-1, ABSTRACT_INSENTIENT),
    ABSTRACT_AGEABLE(-1, ABSTRACT_CREATURE),
    VILLAGER(79, ABSTRACT_AGEABLE),
    ABSTRACT_ANIMAL(-1, ABSTRACT_AGEABLE),
    CHICKEN(7, ABSTRACT_ANIMAL),
    COW(9, ABSTRACT_ANIMAL),
    MOOSHROOM(47, COW),
    PIG(51, ABSTRACT_ANIMAL),
    POLAR_BEAR(54, ABSTRACT_ANIMAL),
    RABBIT(56, ABSTRACT_ANIMAL),
    SHEEP(58, ABSTRACT_ANIMAL),
    TURTLE(73, ABSTRACT_ANIMAL),
    ABSTRACT_TAMEABLE_ANIMAL(-1, ABSTRACT_ANIMAL),
    OCELOT(48, ABSTRACT_TAMEABLE_ANIMAL),
    WOLF(86, ABSTRACT_TAMEABLE_ANIMAL),
    ABSTRACT_PARROT(-1, ABSTRACT_TAMEABLE_ANIMAL),
    PARROT(50, ABSTRACT_PARROT),
    ABSTRACT_HORSE(-1, ABSTRACT_ANIMAL),
    CHESTED_HORSE(-1, ABSTRACT_HORSE),
    DONKEY(11, CHESTED_HORSE),
    MULE(46, CHESTED_HORSE),
    LLAMA(36, CHESTED_HORSE),
    HORSE(29, ABSTRACT_HORSE),
    SKELETON_HORSE(63, ABSTRACT_HORSE),
    ZOMBIE_HORSE(88, ABSTRACT_HORSE),
    ABSTRACT_GOLEM(-1, ABSTRACT_CREATURE),
    SNOW_GOLEM(66, ABSTRACT_GOLEM),
    IRON_GOLEM(80, ABSTRACT_GOLEM),
    SHULKER(59, ABSTRACT_GOLEM),
    ABSTRACT_FISHES(-1, ABSTRACT_CREATURE),
    COD(8, ABSTRACT_FISHES),
    PUFFERFISH(52, ABSTRACT_FISHES),
    SALMON(57, ABSTRACT_FISHES),
    TROPICAL_FISH(72, ABSTRACT_FISHES),
    ABSTRACT_MONSTER(-1, ABSTRACT_CREATURE),
    BLAZE(4, ABSTRACT_MONSTER),
    CREEPER(10, ABSTRACT_MONSTER),
    ENDERMITE(19, ABSTRACT_MONSTER),
    ENDERMAN(18, ABSTRACT_MONSTER),
    GIANT(27, ABSTRACT_MONSTER),
    SILVERFISH(61, ABSTRACT_MONSTER),
    VEX(78, ABSTRACT_MONSTER),
    WITCH(82, ABSTRACT_MONSTER),
    WITHER(83, ABSTRACT_MONSTER),
    ABSTRACT_ILLAGER_BASE(-1, ABSTRACT_MONSTER),
    ABSTRACT_EVO_ILLU_ILLAGER(-1, ABSTRACT_ILLAGER_BASE),
    EVOKER(21, ABSTRACT_EVO_ILLU_ILLAGER),
    ILLUSIONER(31, ABSTRACT_EVO_ILLU_ILLAGER),
    VINDICATOR(81, ABSTRACT_ILLAGER_BASE),
    ABSTRACT_SKELETON(-1, ABSTRACT_MONSTER),
    SKELETON(62, ABSTRACT_SKELETON),
    STRAY(71, ABSTRACT_SKELETON),
    WITHER_SKELETON(84, ABSTRACT_SKELETON),
    GUARDIAN(28, ABSTRACT_MONSTER),
    ELDER_GUARDIAN(15, GUARDIAN),
    SPIDER(69, ABSTRACT_MONSTER),
    CAVE_SPIDER(6, SPIDER),
    ZOMBIE(87, ABSTRACT_MONSTER),
    DROWNED(14, ZOMBIE),
    HUSK(30, ZOMBIE),
    ZOMBIE_PIGMAN(53, ZOMBIE),
    ZOMBIE_VILLAGER(89, ZOMBIE),
    ABSTRACT_FLYING(-1, ABSTRACT_INSENTIENT),
    GHAST(26, ABSTRACT_FLYING),
    PHANTOM(90, ABSTRACT_FLYING),
    ABSTRACT_AMBIENT(-1, ABSTRACT_INSENTIENT),
    BAT(3, ABSTRACT_AMBIENT),
    ABSTRACT_WATERMOB(-1, ABSTRACT_INSENTIENT),
    SQUID(70, ABSTRACT_WATERMOB),
    DOLPHIN(12, ABSTRACT_WATERMOB),
    SLIME(64, ABSTRACT_INSENTIENT),
    MAGMA_CUBE(38, SLIME),
    ABSTRACT_HANGING(-1, ENTITY),
    LEASH_KNOT(35, ABSTRACT_HANGING),
    ITEM_FRAME(33, ABSTRACT_HANGING),
    PAINTING(49, ABSTRACT_HANGING),
    ABSTRACT_LIGHTNING(-1, ENTITY),
    LIGHTNING_BOLT(91, ABSTRACT_LIGHTNING),
    ABSTRACT_ARROW(-1, ENTITY),
    ARROW(2, ABSTRACT_ARROW),
    SPECTRAL_ARROW(68, ABSTRACT_ARROW),
    TRIDENT(94, ABSTRACT_ARROW),
    ABSTRACT_FIREBALL(-1, ENTITY),
    DRAGON_FIREBALL(13, ABSTRACT_FIREBALL),
    FIREBALL(34, ABSTRACT_FIREBALL),
    SMALL_FIREBALL(65, ABSTRACT_FIREBALL),
    WITHER_SKULL(85, ABSTRACT_FIREBALL),
    PROJECTILE_ABSTRACT(-1, ENTITY),
    SNOWBALL(67, PROJECTILE_ABSTRACT),
    ENDER_PEARL(75, PROJECTILE_ABSTRACT),
    EGG(74, PROJECTILE_ABSTRACT),
    POTION(77, PROJECTILE_ABSTRACT),
    EXPERIENCE_BOTTLE(76, PROJECTILE_ABSTRACT),
    MINECART_ABSTRACT(-1, ENTITY),
    CHESTED_MINECART_ABSTRACT(-1, MINECART_ABSTRACT),
    CHEST_MINECART(40, CHESTED_MINECART_ABSTRACT),
    HOPPER_MINECART(43, CHESTED_MINECART_ABSTRACT),
    MINECART(39, MINECART_ABSTRACT),
    FURNACE_MINECART(42, MINECART_ABSTRACT),
    COMMAND_BLOCK_MINECART(41, MINECART_ABSTRACT),
    TNT_MINECART(45, MINECART_ABSTRACT),
    SPAWNER_MINECART(44, MINECART_ABSTRACT),
    BOAT(5, ENTITY);
    
    private static final Map<Integer, EntityType> TYPES = new HashMap<>();
    
    private final int id;
    
    private final EntityType parent;
    
    static {
      for (EntityType type : values())
        TYPES.put(Integer.valueOf(type.id), type); 
    }
    
    EntityType(int id) {
      this.id = id;
      this.parent = null;
    }
    
    EntityType(int id, EntityType parent) {
      this.id = id;
      this.parent = parent;
    }
    
    public int getId() {
      return this.id;
    }
    
    public EntityType getParent() {
      return this.parent;
    }
    
    public static Optional<EntityType> findById(int id) {
      if (id == -1)
        return Optional.empty(); 
      return Optional.ofNullable(TYPES.get(Integer.valueOf(id)));
    }
  }
  
  public enum ObjectType implements ObjectType {
    BOAT(1, Entity1_13Types.EntityType.BOAT),
    ITEM(2, Entity1_13Types.EntityType.ITEM),
    AREA_EFFECT_CLOUD(3, Entity1_13Types.EntityType.AREA_EFFECT_CLOUD),
    MINECART(10, Entity1_13Types.EntityType.MINECART),
    TNT_PRIMED(50, Entity1_13Types.EntityType.TNT),
    ENDER_CRYSTAL(51, Entity1_13Types.EntityType.END_CRYSTAL),
    TIPPED_ARROW(60, Entity1_13Types.EntityType.ARROW),
    SNOWBALL(61, Entity1_13Types.EntityType.SNOWBALL),
    EGG(62, Entity1_13Types.EntityType.EGG),
    FIREBALL(63, Entity1_13Types.EntityType.FIREBALL),
    SMALL_FIREBALL(64, Entity1_13Types.EntityType.SMALL_FIREBALL),
    ENDER_PEARL(65, Entity1_13Types.EntityType.ENDER_PEARL),
    WITHER_SKULL(66, Entity1_13Types.EntityType.WITHER_SKULL),
    SHULKER_BULLET(67, Entity1_13Types.EntityType.SHULKER_BULLET),
    LLAMA_SPIT(68, Entity1_13Types.EntityType.LLAMA_SPIT),
    FALLING_BLOCK(70, Entity1_13Types.EntityType.FALLING_BLOCK),
    ITEM_FRAME(71, Entity1_13Types.EntityType.ITEM_FRAME),
    EYE_OF_ENDER(72, Entity1_13Types.EntityType.EYE_OF_ENDER),
    POTION(73, Entity1_13Types.EntityType.POTION),
    EXPERIENCE_BOTTLE(75, Entity1_13Types.EntityType.EXPERIENCE_BOTTLE),
    FIREWORK_ROCKET(76, Entity1_13Types.EntityType.FIREWORK_ROCKET),
    LEASH(77, Entity1_13Types.EntityType.LEASH_KNOT),
    ARMOR_STAND(78, Entity1_13Types.EntityType.ARMOR_STAND),
    EVOKER_FANGS(79, Entity1_13Types.EntityType.EVOKER_FANGS),
    FISHIHNG_HOOK(90, Entity1_13Types.EntityType.FISHING_BOBBER),
    SPECTRAL_ARROW(91, Entity1_13Types.EntityType.SPECTRAL_ARROW),
    DRAGON_FIREBALL(93, Entity1_13Types.EntityType.DRAGON_FIREBALL),
    TRIDENT(94, Entity1_13Types.EntityType.TRIDENT);
    
    private static final Map<Integer, ObjectType> TYPES = new HashMap<>();
    
    private final int id;
    
    private final Entity1_13Types.EntityType type;
    
    static {
      for (ObjectType type : values())
        TYPES.put(Integer.valueOf(type.id), type); 
    }
    
    ObjectType(int id, Entity1_13Types.EntityType type) {
      this.id = id;
      this.type = type;
    }
    
    public int getId() {
      return this.id;
    }
    
    public Entity1_13Types.EntityType getType() {
      return this.type;
    }
    
    public static Optional<ObjectType> findById(int id) {
      if (id == -1)
        return Optional.empty(); 
      return Optional.ofNullable(TYPES.get(Integer.valueOf(id)));
    }
    
    public static Optional<Entity1_13Types.EntityType> getPCEntity(int id) {
      Optional<ObjectType> output = findById(id);
      if (!output.isPresent())
        return Optional.empty(); 
      return Optional.of(((ObjectType)output.get()).type);
    }
    
    public static Optional<ObjectType> fromEntityType(Entity1_13Types.EntityType type) {
      for (ObjectType ent : values()) {
        if (ent.type == type)
          return Optional.of(ent); 
      } 
      return Optional.empty();
    }
  }
}
