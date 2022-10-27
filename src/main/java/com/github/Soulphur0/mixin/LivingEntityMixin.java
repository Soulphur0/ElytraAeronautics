package com.github.Soulphur0.mixin;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.ConfigFileReader;
import com.github.Soulphur0.config.EanConfigFile;
import com.github.Soulphur0.utility.EanMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    // * Config file data
    EanConfigFile configFile = ConfigFileReader.getConfigFile();

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world){
        super(entityType,world);
    }

    @ModifyArg(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 6))
    private Vec3d modifyVelocity(Vec3d vector){
        // ? Re-read config file if data has been modified.
        if (ElytraAeronautics.readConfigFileCue_LivingEntityMixin){
            configFile = ConfigFileReader.getConfigFile();
            ElytraAeronautics.readConfigFileCue_LivingEntityMixin = false;
        }

        // ? Gradual pitch realignment
        if(configFile.isSneakRealignsPitch() && this.isSneaking()){
            float pitch = this.getPitch();

            float alignmentAngle = configFile.getRealignmentAngle();
            float alignementRate = configFile.getRealignmentRate();

            if (Math.abs(pitch) <= alignementRate*2){
                this.setPitch(alignmentAngle);
            } else {
                if (pitch > alignmentAngle){
                    this.setPitch(pitch-alignementRate);
                } else {
                    this.setPitch(pitch+alignementRate);
                }
            }
        }

        // ? Get player altitude
        Vec3d positionVector = super.getPos();
        double playerAltitude = positionVector.y;

        // ? Calculate player speed based on altitude and return
        Vec3d movementVector;
        movementVector = calcMovementVector(playerAltitude);
        return movementVector.multiply(0.99f, 0.98f, 0.99f);
    }

    private Vec3d calcMovementVector(double playerAltitude){
        double speedConstant = 0.08;
        double aux;
        double aux2;

        // * Calculate additional speed based on player altitude.
        double minSpeed = configFile.getMinSpeed();
        double maxSpeed = configFile.getMaxSpeed();
        double curveStart = configFile.getCurveStart();
        double curveEnd = configFile.getCurveEnd();
        double additionalSpeed;

        if (configFile.isAltitudeDeterminesSpeed() && curveStart < playerAltitude)
            additionalSpeed = EanMath.getLinealValue(curveStart,Math.sqrt(minSpeed),curveEnd,Math.sqrt(maxSpeed),playerAltitude);
        else
            additionalSpeed = minSpeed;

        Vec3d movementVector = this.getVelocity();
        if (movementVector.y > -0.5) {
            this.fallDistance = 1.0f;
        }

        Vec3d rotationVector = this.getRotationVector();
        float pitchInRadians = this.getPitch() * ((float)Math.PI / 180);
        double angleToTheGround = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        double speed = movementVector.horizontalLength();
        double rotationVectorLength = rotationVector.length();

        float fallSpeedMultiplier = MathHelper.cos(pitchInRadians);
        fallSpeedMultiplier = (float)((double)fallSpeedMultiplier * ((double)fallSpeedMultiplier * Math.min(1.0, rotationVectorLength / 0.4)));

        movementVector = this.getVelocity().add(0.0, speedConstant * (-1.0 + (double)fallSpeedMultiplier * 0.75), 0.0);

        // * Looking under the horizon
        // Horizontal movement uses aux plus the (+1 m/s) constant multiplied by the speed set by the player minus default speed.
        if (movementVector.y < 0.0 && angleToTheGround > 0.0) {
            aux = movementVector.y * -0.1 * (double)fallSpeedMultiplier;
            aux2 = aux + (additionalSpeed-30.35)*0.0005584565076792029D;

            movementVector = movementVector.add(rotationVector.x * aux2 / angleToTheGround, aux, rotationVector.z * aux2 / angleToTheGround);
        }

        // * Looking over the horizon
        // Vertical speed decreases with the player realtime speed.
        if (pitchInRadians < 0.0f && angleToTheGround > 0.0) {
            aux = speed * (double)(-MathHelper.sin(pitchInRadians)) * 0.04;

            movementVector = movementVector.add(-rotationVector.x * aux / angleToTheGround, Math.min((aux * 3.2), 0.1D), -rotationVector.z * aux / angleToTheGround);
        }

        if (angleToTheGround > 0.0) {
            movementVector = movementVector.add((rotationVector.x / angleToTheGround * speed - movementVector.x) * 0.1, 0.0, (rotationVector.z / angleToTheGround * speed - movementVector.z) * 0.1);
        }

        return movementVector;
    }
}
