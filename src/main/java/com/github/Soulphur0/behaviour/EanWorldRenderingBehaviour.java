package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
public class EanWorldRenderingBehaviour {

    public static boolean checkWorldUnloadingConditions(ClientPlayerEntity player, double speed, double altitude){
        WorldRenderingConfig configInstance = WorldRenderingConfig.getOrCreateInstance();
        boolean canLoadChunks;

        if (player.isFallFlying()){
            // Yes, the AND in the OR option and the OR in the AND option have to be like that, as confusing as it might seem.
            canLoadChunks = switch (WorldRenderingConfig.getOrCreateInstance().getChunkUnloadingCondition()){
                case SPEED -> speed < configInstance.getUnloadingSpeed();
                case HEIGHT -> altitude < configInstance.getUnloadingHeight();
                case SPEED_OR_HEIGHT -> speed < configInstance.getUnloadingSpeed() && altitude < configInstance.getUnloadingHeight();
                case SPEED_AND_HEIGHT -> speed < configInstance.getUnloadingSpeed() || altitude < configInstance.getUnloadingHeight();
            };
        } else {
            canLoadChunks = true;
        }

        return canLoadChunks;
    }
}
