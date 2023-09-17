package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    // _ World class' getBlockState() bypass.
    @WrapWithCondition(method ="tick", at = @At(value ="INVOKE", target ="Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V"))
    private boolean ean_skipUpdateTargetedEntity(GameRenderer instance, float tickDelta){
        return EanClientPlayerData.hasChunkLoadingAbility();
    }
}
