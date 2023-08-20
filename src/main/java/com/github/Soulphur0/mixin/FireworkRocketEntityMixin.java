package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.utility.EanMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin   {

    @Shadow private @Nullable LivingEntity shooter;

    FlightConfig configInstance = FlightConfig.getOrCreateInstance();

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private Vec3d ean_modifyRocketBoostVelocity(Vec3d par1){

        if (configInstance.isAltitudeDeterminesSpeed()){
            // $ Get movement and position values from the shooter
            Vec3d positionVector = shooter.getPos();
            Vec3d shooterRotation = shooter.getRotationVector();
            Vec3d shooterVelocity = shooter.getVelocity();
            double shooterAltitude = positionVector.y;

            // $ Calculate additional speed boost
            // ? Read config file values
            double minSpeed = configInstance.getMinSpeed();
            double maxSpeed = configInstance.getMaxSpeed();
            double curveStart = configInstance.getMinHeight();
            double curveEnd = configInstance.getMaxHeight();

            // + Calculate additional speed based on player altitude.
            // * Clamp the calculated modified speed to not be below or over the speed range.
            double altitudeCalculatedSpeed = (configInstance.isAltitudeDeterminesSpeed()) ? MathHelper.clamp(EanMath.getLinealValue(curveStart,minSpeed,curveEnd,maxSpeed,shooterAltitude), minSpeed, maxSpeed) : minSpeed;

            // + Equation that determines an estimate for which speedMultiplier to apply at different theoretical speed altitudes.
            double speedMultiplier = 0.0000006453840919839 * Math.pow(altitudeCalculatedSpeed, 2) + 0.0508467 * altitudeCalculatedSpeed - 0.202377;

            // $ Apply additional speed to the shooter's movement
            return shooterVelocity.add(
                    shooterRotation.x * 0.1 + (shooterRotation.x * speedMultiplier - shooterVelocity.x) * 0.5,
                    shooterRotation.y * 0.1 + (shooterRotation.y * speedMultiplier - shooterVelocity.y) * 0.5,
                    shooterRotation.z * 0.1 + (shooterRotation.z * speedMultiplier - shooterVelocity.z) * 0.5);
        } else {
            return par1;
        }
    }
}
