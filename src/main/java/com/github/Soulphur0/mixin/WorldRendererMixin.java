package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", ordinal = 1))
    private void ean_renderClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f positionMatrix, float tickDelta, double x, double y, double z, Operation<Void> original){
        if (CloudConfig.getOrCreateInstance().isUseEanClouds())
            EanCloudRenderBehaviour.ean_renderClouds(instance, matrices, positionMatrix, tickDelta, x, y, z);
        else
            original.call(instance, matrices, positionMatrix, tickDelta, x, y, z);
    }

    // _ World class' getBlockState() bypass.
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;updateChunks(Lnet/minecraft/client/render/Camera;)V"))
    private boolean ean_canUpdateChunks(WorldRenderer instance, Camera camera){
        return EanClientPlayerData.hasChunkLoadingAbility();
    }

    // _ World class' getFluidState() bypass.
    @WrapOperation(method ="renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value ="INVOKE", target ="Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"))
    private CameraSubmersionType ean_bypassCameraGetSubmersionType(Camera instance, Operation<CameraSubmersionType> original){
        if (!EanClientPlayerData.hasChunkLoadingAbility())
            return CameraSubmersionType.NONE;
        else
            return original.call(instance);
    }
}