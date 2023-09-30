package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.server.EanRocketBoostBehaviour;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin   {

    @Shadow private @Nullable LivingEntity shooter;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private Vec3d ean_modifyRocketBoostVelocity(Vec3d original){
        return EanRocketBoostBehaviour.calcFireworkRocketBoost(shooter, original);
    }
}
