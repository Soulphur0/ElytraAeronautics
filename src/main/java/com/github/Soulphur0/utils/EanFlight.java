package com.github.Soulphur0.utils;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class EanFlight {

    public static double getAltitudeCalculatedSpeed(LivingEntity shooter) {
        FlightConfig configInstance = FlightConfig.getOrCreateInstance();
        double shooterAltitude = shooter.getPos().y;

        // + Read config file values
        double minSpeed = configInstance.getMinSpeed();
        double maxSpeed = configInstance.getMaxSpeed();
        double curveStart = configInstance.getMinHeight();
        double curveEnd = configInstance.getMaxHeight();

        // + Calculate additional speed based on player altitude.
        // * Clamp the calculated modified speed to not be below or over the speed range.
        return (configInstance.isAltitudeDeterminesSpeed()) ? MathHelper.clamp(EanMath.getLinealValue(curveStart,minSpeed,curveEnd,maxSpeed,shooterAltitude), minSpeed, maxSpeed) : minSpeed;
    }
}
