package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // _ World class' getBlockState() bypass.
    @WrapWithCondition(method ="renderWorld", at = @At(value ="INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V"))
    private boolean ean_cancelUpdateTargetedEntity(GameRenderer instance, float tickDelta){
        return EanClientPlayerData.hasChunkLoadingAbility();
    }

    // _ World class' getBlockState() bypass.
    @WrapWithCondition(method ="renderHand", at = @At(value ="INVOKE", target ="Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderOverlays(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private boolean ean_skipRenderOverlays(MinecraftClient client, MatrixStack matrices){
        return EanClientPlayerData.hasChunkLoadingAbility();
    }

    // _ World class' getFluidState() bypass.
    @WrapOperation(method ="getFov", at = @At(value ="INVOKE", target ="Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"))
    private CameraSubmersionType ean_bypassGetSubmerssionType(Camera instance, Operation<CameraSubmersionType> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance);
        else
            return CameraSubmersionType.NONE;
    }


}
