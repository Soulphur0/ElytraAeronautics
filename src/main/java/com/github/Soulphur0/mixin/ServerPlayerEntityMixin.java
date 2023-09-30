package com.github.Soulphur0.mixin;

import com.github.Soulphur0.networking.EanPlayerDataCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    // _ World class' getBlockState() bypass.
    @ModifyExpressionValue(method ="applyMovementEffects", at = @At(value = "INVOKE", target ="Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    private boolean ean_bypassApplyMovementEffects(boolean original){
        ServerPlayerEntity player = ((ServerPlayerEntity)(Object)this);
        return original && EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
    }
}
