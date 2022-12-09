package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.ConfigFileReader;
import com.github.Soulphur0.config.EanConfigFile;
import com.github.Soulphur0.utility.EanMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EanFlightBehaviour {

    static public Vec3d flightBehaviour(LivingEntity player){
        return modifyVelocity(player);
    }

    // * Config file data
    private static EanConfigFile configFile = ConfigFileReader.getConfigFile();

    private static Vec3d modifyVelocity(LivingEntity player){
        // ? Re-read config file if data has been modified.
        if (ElytraAeronautics.readConfigFileCue_LivingEntityMixin){
            configFile = ConfigFileReader.getConfigFile();
            ElytraAeronautics.readConfigFileCue_LivingEntityMixin = false;
        }

        // ? Gradual pitch realignment
        if(configFile.isSneakRealignsPitch() && player.isSneaking()){
            float pitch = player.getPitch();

            float alignmentAngle = configFile.getRealignmentAngle();
            float alignementRate = configFile.getRealignmentRate();

            if (Math.abs(pitch) <= alignementRate*2){
                player.setPitch(alignmentAngle);
            } else {
                if (pitch > alignmentAngle){
                    player.setPitch(pitch-alignementRate);
                } else {
                    player.setPitch(pitch+alignementRate);
                }
            }
        }

        // ? Get player altitude
        Vec3d positionVector = player.getPos();
        double playerAltitude = positionVector.y;

        // ? Calculate player speed based on altitude and return
        Vec3d movementVector;
        movementVector = calcMovementVector(player, playerAltitude);
        return movementVector.multiply(0.99f, 0.98f, 0.99f);
    }

    static private Vec3d calcMovementVector(LivingEntity player, double playerAltitude){
        double speedConstant = 0.08;
        double aux;
        double aux2;

        // * Calculate additional speed based on player altitude.
        double minSpeed = configFile.getMinSpeed();
        double maxSpeed = configFile.getMaxSpeed();
        double curveStart = configFile.getCurveStart();
        double curveEnd = configFile.getCurveEnd();
        double modHSpeed;

        // * Clamp the calculated modified speed to not be below or over the speed range.
        modHSpeed = (configFile.isAltitudeDeterminesSpeed()) ? MathHelper.clamp(EanMath.getLinealValue(curveStart,minSpeed,curveEnd,maxSpeed,playerAltitude), minSpeed, maxSpeed) : minSpeed;

        Vec3d movementVector = player.getVelocity();
        if (movementVector.y > -0.5) {
            player.fallDistance = 1.0f;
        }

        Vec3d rotationVector = player.getRotationVector();
        float pitchInRadians = player.getPitch() * ((float)Math.PI / 180);
        double angleToTheGround = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        double speed = movementVector.horizontalLength();
        double rotationVectorLength = rotationVector.length();

        float fallSpeedMultiplier = MathHelper.cos(pitchInRadians);
        fallSpeedMultiplier = (float)((double)fallSpeedMultiplier * ((double)fallSpeedMultiplier * Math.min(1.0, rotationVectorLength / 0.4)));

        movementVector = player.getVelocity().add(0.0, speedConstant * (-1.0 + (double)fallSpeedMultiplier * 0.75), 0.0); // ! Set Y=0.0 to turn off downwards speed.

        // * Looking under the horizon
        // Horizontal movement uses aux plus the (+1 m/s) constant multiplied by the speed set by the player minus default speed.
        if (movementVector.y < 0.0 && angleToTheGround > 0.0) {
            aux = movementVector.y * -0.1 * (double)fallSpeedMultiplier;

            // ! The value 30.1298 should only be subtracted when downwards speed is active, since this speed affects the horizontal speed that it is already tweaked to almost perfectly reflect config file values.
            aux2 = aux + (modHSpeed-30.1298D)*0.0005584565076792029D; // ? This is the 1 m/s constant, it is not 100% accurate, but it is close enough.

            movementVector = movementVector.add(rotationVector.x * aux2 / angleToTheGround, aux, rotationVector.z * aux2 / angleToTheGround); // ! Set Y=0.0 to turn off downwards speed.
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
