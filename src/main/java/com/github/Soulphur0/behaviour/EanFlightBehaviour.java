package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.config.EanServerSettings;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
import com.github.Soulphur0.utility.EanMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EanFlightBehaviour {

    // : Flight entry point for injection/modification.
    static public Vec3d ean_flightBehaviour(LivingEntity player){
        return ean_loadAndSyncFlightConfiguration(player);
    }

    // : Read FlightConfig from disk and sync it with the client.
    private static Vec3d ean_loadAndSyncFlightConfiguration(LivingEntity player){

        // . Order the client to sync settings with the server if the config has changed.
        if (!player.getWorld().isClient()) {
            if (EanServerSettings.settingsChanged){
                EanServerPacketSender.syncAllClientsConfigWithServer((PlayerEntity) player);
                EanServerSettings.settingsChanged = false;
            }
        }

        // + Get config instance.
        FlightConfig configInstance = FlightConfig.getOrCreateInstance();

        return ean_setupFlightCalc(player, configInstance);
    }

    // : Calculations.
    private static Vec3d ean_setupFlightCalc(LivingEntity player, FlightConfig configInstance){

        // + Gradual pitch realignment
        if(configInstance.isSneakingRealignsPitch() && player.isSneaking()){
            float pitch = player.getPitch();

            float alignmentAngle = configInstance.getRealignAngle();
            float alignementRate = configInstance.getRealignRate();

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

        // = Return null vector if altitude-determined speed is disabled
        if (!configInstance.isAltitudeDeterminesSpeed())
            return null;

        // + Get player altitude
        Vec3d positionVector = player.getPos();
        double playerAltitude = positionVector.y;

        // % Calculate player speed based on altitude and return
        Vec3d movementVector = ean_calcFlightMovementVector(configInstance, player, playerAltitude);

        return movementVector.multiply(0.99f, 0.98f, 0.99f);
    }

    private static Vec3d ean_calcFlightMovementVector(FlightConfig configInstance, LivingEntity player, double playerAltitude){
        double fallSpeedConstant = 0.08;
        double verticalSpeedValue;
        double horizontalSpeedValue;

        // ? Read config file values
        double minSpeed = configInstance.getMinSpeed();
        double maxSpeed = configInstance.getMaxSpeed();
        double curveStart = configInstance.getMinHeight();
        double curveEnd = configInstance.getMaxHeight();

        // + Calculate additional speed based on player altitude.
        // * Clamp the calculated modified speed to not be below or over the speed range.
        double altitudeCalculatedSpeed = (configInstance.isAltitudeDeterminesSpeed()) ? MathHelper.clamp(EanMath.getLinealValue(curveStart,minSpeed,curveEnd,maxSpeed,playerAltitude), minSpeed, maxSpeed) : minSpeed;

        Vec3d movementVector = player.getVelocity();
        if (movementVector.y > -0.5) {
            player.fallDistance = 1.0f;
        }

        Vec3d rotationVector = player.getRotationVector();
        float pitchInRadians = player.getPitch() * ((float)Math.PI / 180);
        double angleToTheGround = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        double speed = movementVector.horizontalLength();
        double rotationVectorLength = rotationVector.length();

        // $ Vertical speed calculations

        // + Calculate the fall speed multiplier based on the player's flight pitch.
        float fallSpeedMultiplier = MathHelper.cos(pitchInRadians);
        fallSpeedMultiplier = (float)((double)fallSpeedMultiplier * ((double)fallSpeedMultiplier * Math.min(1.0, rotationVectorLength / 0.4)));

        // + Make the player always lose altitude.
        // * A greater flight pitch and therefore fallSpeedMultiplier, lead to a greater downwards vertical velocity.
        // % Set Y=0.0 to turn off downwards speed.
        movementVector = player.getVelocity().add(0.0, fallSpeedConstant * (-1.0 + (double)fallSpeedMultiplier * 0.75), 0.0);

        // $ Horizontal speed and movement vector calculations

        // + Looking under the horizon
        // * Horizontal movement uses verticalSpeedValue plus the (+1 m/s) constant multiplied by the speed set by the player (minus default speed).
        if (movementVector.y < 0.0 && angleToTheGround > 0.0) {
            verticalSpeedValue = movementVector.y * -0.1 * (double)fallSpeedMultiplier;

            // ! Total Speed: The configured speed value minus the vanilla speed value.
            // % The value 30.1298 should only be subtracted when downwards speed (fall speed) is active, since this speed affects the horizontal speed that it is already tweaked to almost perfectly reflect config file values.
            double totalSpeed = (altitudeCalculatedSpeed-30.1298D);

            // ? Horizontal speed value: vertical speed is added to the total speed multiplied by the 1 m/s constant.
            // ¿ The 1 m/s constant is not 100% accurate, but it is close enough.
            horizontalSpeedValue = verticalSpeedValue + totalSpeed*0.0005584565076792029D;

            // % Set Y=0.0 to turn off downwards speed.
            movementVector = movementVector.add(rotationVector.x * horizontalSpeedValue / angleToTheGround, verticalSpeedValue, rotationVector.z * horizontalSpeedValue / angleToTheGround);
        }

        // + Looking over the horizon
        // * Vertical speed decreases with the player realtime speed.
        if (pitchInRadians < 0.0f && angleToTheGround > 0.0) {
            verticalSpeedValue = speed * (double)(-MathHelper.sin(pitchInRadians)) * 0.04;

            movementVector = movementVector.add(-rotationVector.x * verticalSpeedValue / angleToTheGround, Math.min((verticalSpeedValue * 3.2), 0.1D), -rotationVector.z * verticalSpeedValue / angleToTheGround);
        }

        if (angleToTheGround > 0.0) {
            movementVector = movementVector.add((rotationVector.x / angleToTheGround * speed - movementVector.x) * 0.1, 0.0, (rotationVector.z / angleToTheGround * speed - movementVector.z) * 0.1);
        }

        return movementVector;
    }
}
