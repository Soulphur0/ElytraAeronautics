package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanPlayerDataCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ThreadedAnvilChunkStorage.class, priority = 1500)
public abstract class ThreadedAnvilStorageMixin {

    @WrapOperation(method = "handlePlayerAddedOrRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;doesNotGenerateChunks(Lnet/minecraft/server/network/ServerPlayerEntity;)Z"))
    private boolean ean_disableChunkGeneration_1(ThreadedAnvilChunkStorage instance, ServerPlayerEntity player, Operation<Boolean> original){
        boolean condition = EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        return !condition;
    }

    @WrapOperation(method = "updatePosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;doesNotGenerateChunks(Lnet/minecraft/server/network/ServerPlayerEntity;)Z"))
    private boolean ean_disableChunkGeneration_2(ThreadedAnvilChunkStorage instance, ServerPlayerEntity player, Operation<Boolean> original){
        boolean condition = EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        return !condition;
    }
}