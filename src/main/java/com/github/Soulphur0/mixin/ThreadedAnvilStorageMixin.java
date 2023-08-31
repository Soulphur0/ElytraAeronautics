package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilStorageMixin {


    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true)
    private void ean_disableUpdatePosition(ServerPlayerEntity player, CallbackInfo ci){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            ci.cancel();
    }

    @WrapWithCondition(method = "getChunk", at = @At(value = "INVOKE", target ="Lnet/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager;addTicketWithLevel(Lnet/minecraft/server/world/ChunkTicketType;Lnet/minecraft/util/math/ChunkPos;ILjava/lang/Object;)V"))
    private boolean ean_plsBeThis(ThreadedAnvilChunkStorage.TicketManager instance, ChunkTicketType ticketType, ChunkPos chunkPos, int level, Object argument){
        return FlightConfig.getOrCreateInstance().isSneakingRealignsPitch();
    }

    @Inject(method ="loadEntity", at = @At("HEAD"), cancellable = true)
    private void ean_test10000(Entity entity, CallbackInfo ci){
        if(FlightConfig.getOrCreateInstance().getRealignAngle() == 69)
            ci.cancel();
    }

    @Inject(method = "canTickChunk", at = @At("HEAD"), cancellable = true)
    private void ean_disableCanTickChunk(ServerPlayerEntity player, ChunkPos pos, CallbackInfoReturnable<Boolean> cir){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            cir.setReturnValue(false);
    }

    @ModifyReturnValue(method="doesNotGenerateChunks", at = @At("RETURN"))
    private boolean ean_addChunkGenerationCondition(boolean original){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            return true;
        else
            return original;
    }
}