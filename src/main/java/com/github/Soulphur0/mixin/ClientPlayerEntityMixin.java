package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanWorldRenderingBehaviour;
import com.github.Soulphur0.networking.client.EanClientPacketSender;
import com.github.Soulphur0.utility.EanClientPlayerData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    Vec3d lastPos = Vec3d.ZERO;
    double playerSpeed;
    double playerAltitude;

    // ? Player speed is calculated on the client and sent to the server.
    // ¿ Otherwise due to sync inconsistencies the calculated value is increasingly wrong as speed goes up.
    @Inject(method = "tick", at = @At("HEAD"))
    private void ean_determinePlayerSpeed(CallbackInfo ci){
        ClientPlayerEntity thisInstance = ((ClientPlayerEntity)(Object)this);

        // _ Skip calculations and server communication if the player is not elytra-flying.
        if (!thisInstance.isFallFlying()){
            EanClientPlayerData.setHasChunkLoadingAbility(true);
            EanClientPacketSender.sendPlayerChunkLoadingAbility(thisInstance.getUuid(), true);
            return;
        }

        // + Calculate speed & get altitude
        Vec3d pos = thisInstance.getPos();
        playerSpeed = Math.round(Math.sqrt(Math.pow(pos.x-lastPos.x,2) + Math.pow(pos.y-lastPos.y,2) + Math.pow(pos.z-lastPos.z,2)) * 20 /* 20 ticks every second */ * 100.0) / 100.0;
        lastPos = pos;
        playerAltitude = thisInstance.getPos().getY();

        // + Check if world unloading conditions match & send whether this player (specified UUID) can load chunks.
        boolean canLoadChunks = EanWorldRenderingBehaviour.checkWorldUnloadingConditions(thisInstance, playerSpeed, playerAltitude);

        // + Update in the client and send to the server.
        EanClientPlayerData.setHasChunkLoadingAbility(canLoadChunks);
        EanClientPacketSender.sendPlayerChunkLoadingAbility(thisInstance.getUuid(), canLoadChunks);
    }
}
