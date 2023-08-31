package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

    @ModifyVariable(method="getChunkFuture", at = @At("HEAD"), ordinal = 0)
    private boolean changeCreate(boolean value){
        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            return false;
        else
            return value;
    }

    @ModifyVariable(method="getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), ordinal = 0)
    private boolean changeCreate2(boolean value){
        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            return false;
        else
            return value;
    }
}
