package com.github.Soulphur0.behaviour.server;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.entity.LivingEntity;

public class EanPitchRealignmentBehaviour {

    public static void realignPitch(LivingEntity player){
        FlightConfig configInstance = FlightConfig.getOrCreateInstance();

        if(configInstance.isSneakingRealignsPitch() && player.isSneaking()){
            float pitch = player.getPitch();

            float alignmentAngle = configInstance.getRealignAngle();
            float alignmentRate = configInstance.getRealignRate();

            if (Math.abs(pitch) <= alignmentRate*2){
                player.setPitch(alignmentAngle);
            } else {
                if (pitch > alignmentAngle){
                    player.setPitch(pitch-alignmentRate);
                } else {
                    player.setPitch(pitch+alignmentRate);
                }
            }
        }
    }
}
