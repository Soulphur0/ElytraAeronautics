package com.github.Soulphur0.mixin;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.ConfigFileReader;
import com.github.Soulphur0.config.EanConfigFile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    // * Config file data
    EanConfigFile configFile = ConfigFileReader.getConfigFile();

    private Vec3d movementVector;

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world){
        super(entityType,world);
    }

    @ModifyVariable(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At("STORE"), ordinal = 1)
    private Vec3d getMovementVector(Vec3d vector) {
        movementVector = vector;
        return vector;
    }

    @ModifyArg(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 6))
    private Vec3d modifyVelocity(Vec3d vector){
        // ? Re-read config file if data has been modified.
        if (ElytraAeronautics.readConfigFileCue_LivingEntityMixin){
            configFile = ConfigFileReader.getConfigFile();
            ElytraAeronautics.readConfigFileCue_LivingEntityMixin = false;
        }

        final double maxAddedMultiplier = configFile.getSpeedConstantAdditionalValue(); // 0.007875=defalut 0.0088=257ms 0.009=310ms
        Vec3d positionVector = super.getPos();
        double playerAltitude = positionVector.y;
        double divider = calcDivider(playerAltitude, configFile.getCurveStart(), configFile.getCurveMiddle(), configFile.getCurveEnd());
        return movementVector.multiply(0.9900000095367432D + maxAddedMultiplier/divider, 0.9800000190734863D, 0.9900000095367432D + maxAddedMultiplier/divider);
    }

    private double getFirstCurveDivider(double curveStart, double curveEnd, double altitude){
        // Curve's Limit points
        double pointA_X = curveStart;
        double pointA_Y = 10; // Sqr(100) -> double

        double pointB_X = curveEnd;
        double pointB_Y = 1.118033988749895; // Sqr(1.25) -> double

        // Calc the curve's slope and intercept
        double slope = (pointB_Y-pointA_Y)/(pointB_X-pointA_X);
        double intercept = 1 - slope*curveEnd;

        // Return the equation's value squared
        return Math.pow(slope*altitude+intercept,2);
    }

    private double getSecondCurveDivider(double curveStart, double curveEnd, double altitude){
        // Curve's Limit points
        double pointA_X = curveStart;
        double pointA_Y = 1.118033988749895; // Sqr(1.25) -> double

        double pointB_X = curveEnd;
        double pointB_Y = 1;

        // Calc the curve's slope and intercept
        double slope = (pointB_Y-pointA_Y)/(pointB_X-pointA_X);
        double intercept = 1 - slope*curveEnd;

        // Return the equation's value squared
        return Math.pow(slope*altitude+intercept,2);
    }

    private double calcDivider(double altitude, double curveStart, double inflectionPoint, double curveEnd){
        if (altitude < curveStart)
            return Double.MAX_VALUE;
        else if (altitude > curveStart && altitude < inflectionPoint)
            return getFirstCurveDivider(curveStart,inflectionPoint,altitude);
        else if (altitude > inflectionPoint && altitude < curveEnd)
            return getSecondCurveDivider(inflectionPoint,curveEnd,altitude);
        else
            return 1;
    }
}
