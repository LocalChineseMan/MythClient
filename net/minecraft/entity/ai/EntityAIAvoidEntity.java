package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
  private final Predicate<Entity> canBeSeenSelector;
  
  protected EntityCreature theEntity;
  
  private final double farSpeed;
  
  private final double nearSpeed;
  
  protected T closestLivingEntity;
  
  private final float avoidDistance;
  
  private PathEntity entityPathEntity;
  
  private final PathNavigate entityPathNavigate;
  
  private final Class<T> field_181064_i;
  
  private final Predicate<? super T> avoidTargetSelector;
  
  public EntityAIAvoidEntity(EntityCreature p_i46404_1_, Class<T> p_i46404_2_, float p_i46404_3_, double p_i46404_4_, double p_i46404_6_) {
    this(p_i46404_1_, p_i46404_2_, Predicates.alwaysTrue(), p_i46404_3_, p_i46404_4_, p_i46404_6_);
  }
  
  public EntityAIAvoidEntity(EntityCreature p_i46405_1_, Class<T> p_i46405_2_, Predicate<? super T> p_i46405_3_, float p_i46405_4_, double p_i46405_5_, double p_i46405_7_) {
    this.canBeSeenSelector = new Predicate<Entity>() {
        public boolean apply(Entity p_apply_1_) {
          return (p_apply_1_.isEntityAlive() && EntityAIAvoidEntity.this.theEntity.getEntitySenses().canSee(p_apply_1_));
        }
      };
    this.theEntity = p_i46405_1_;
    this.field_181064_i = p_i46405_2_;
    this.avoidTargetSelector = p_i46405_3_;
    this.avoidDistance = p_i46405_4_;
    this.farSpeed = p_i46405_5_;
    this.nearSpeed = p_i46405_7_;
    this.entityPathNavigate = p_i46405_1_.getNavigator();
    setMutexBits(1);
  }
  
  public boolean shouldExecute() {
    List<T> list = this.theEntity.worldObj.getEntitiesWithinAABB(this.field_181064_i, this.theEntity.getEntityBoundingBox().expand(this.avoidDistance, 3.0D, this.avoidDistance), Predicates.and(new Predicate[] { EntitySelectors.NOT_SPECTATING, this.canBeSeenSelector, this.avoidTargetSelector }));
    if (list.isEmpty())
      return false; 
    this.closestLivingEntity = list.get(0);
    Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, new Vec3(((Entity)this.closestLivingEntity).posX, ((Entity)this.closestLivingEntity).posY, ((Entity)this.closestLivingEntity).posZ));
    if (vec3 == null)
      return false; 
    if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity((Entity)this.theEntity))
      return false; 
    this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    return (this.entityPathEntity != null && this.entityPathEntity.isDestinationSame(vec3));
  }
  
  public boolean continueExecuting() {
    return !this.entityPathNavigate.noPath();
  }
  
  public void startExecuting() {
    this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
  }
  
  public void resetTask() {
    this.closestLivingEntity = null;
  }
  
  public void updateTask() {
    if (this.theEntity.getDistanceSqToEntity((Entity)this.closestLivingEntity) < 49.0D) {
      this.theEntity.getNavigator().setSpeed(this.nearSpeed);
    } else {
      this.theEntity.getNavigator().setSpeed(this.farSpeed);
    } 
  }
}
