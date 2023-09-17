package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    // _ World class' getFluidState() bypass.
    @WrapOperation(method = "render", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"))
    private static CameraSubmersionType ean_bypassGetSubmersionType_1(Camera instance, Operation<CameraSubmersionType> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance);
        else
            return CameraSubmersionType.NONE;
    }

    // _ World class' getFluidState() bypass.
    @WrapOperation(method ="applyFog", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"))
    private static CameraSubmersionType ean_bypassGetSubmersionType_2(Camera instance, Operation<CameraSubmersionType> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance);
        else
            return CameraSubmersionType.NONE;
    }
}
