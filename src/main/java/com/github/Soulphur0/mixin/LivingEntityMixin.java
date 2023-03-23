package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanFlightBehaviour;
import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world){
        super(entityType,world);
    }

    @ModifyArg(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 6))
    private Vec3d modifyVelocity(Vec3d vector){
        if (FlightConfig.getOrCreateInstance().isAltitudeDeterminesSpeed())
            return EanFlightBehaviour.ean_flightBehaviour(((LivingEntity)(Object)this));
        else
            return vector;
    }
}
