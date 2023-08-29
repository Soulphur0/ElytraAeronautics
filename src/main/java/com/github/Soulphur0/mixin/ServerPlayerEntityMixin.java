package com.github.Soulphur0.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method="tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;update()V"))
    private void ean_disableChunkWatchingAndGeneration(CallbackInfo ci){
        ServerWorld world = ((ServerPlayerEntity)(Object)this).getWorld();
        //world.getChunkManager().getThreadedAnvilChunkStorage();
    }
}
