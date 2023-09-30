package com.github.Soulphur0.behaviour.server;

import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.utils.EanFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class EanRocketBoostBehaviour {

    public static Vec3d calcFireworkRocketBoost(LivingEntity shooter, Vec3d... original){
        FlightConfig configInstance = FlightConfig.getOrCreateInstance();

        if (!configInstance.isAltitudeDeterminesSpeed() && original != null)
            return original[0];

        Vec3d shooterRotation = shooter.getRotationVector();
        Vec3d shooterVelocity = shooter.getVelocity();

        double speedMultiplier = 1.5;

        // + If altitude determines speed, calc a speed multiplier to apply to the return vector.
        // * The equation below determines an estimate for which speedMultiplier to apply at different theoretical speed altitudes.
        if (configInstance.isAltitudeDeterminesSpeed()){
            double altitudeCalculatedSpeed = EanFlight.getAltitudeCalculatedSpeed(shooter);
            speedMultiplier = 0.0000006453840919839 * Math.pow(altitudeCalculatedSpeed, 2) + 0.0508467 * altitudeCalculatedSpeed - 0.202377;
        }

        // ? Return simulated original method call.
        // ¿ If altitudeDeterminesSpeed is disabled, and the method is called from the event callback.
        return shooterVelocity.add(
                shooterRotation.x * 0.1 + (shooterRotation.x * speedMultiplier - shooterVelocity.x) * 0.5,
                shooterRotation.y * 0.1 + (shooterRotation.y * speedMultiplier - shooterVelocity.y) * 0.5,
                shooterRotation.z * 0.1 + (shooterRotation.z * speedMultiplier - shooterVelocity.z) * 0.5);
    }
}
