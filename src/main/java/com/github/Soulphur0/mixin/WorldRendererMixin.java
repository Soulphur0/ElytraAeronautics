package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", ordinal = 1))
    private void ean_renderClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f positionMatrix, float tickDelta, double x, double y, double z, Operation<Void> original){
        if (CloudConfig.getOrCreateInstance().isUseEanClouds())
            EanCloudRenderBehaviour.ean_renderClouds(instance, matrices, positionMatrix, tickDelta, x, y, z);
        else
            original.call(instance, matrices, positionMatrix, tickDelta, x, y, z);
    }

    @Inject(method = "updateChunks", at = @At("HEAD"), cancellable = true)
    private void ean_a(Camera camera, CallbackInfo ci){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            ci.cancel();
    }
}