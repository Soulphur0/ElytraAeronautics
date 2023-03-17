package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    // $ Capture arguments of the original render call.
    BufferBuilder bufferBuilder;
    double x;
    double y;
    double z;
    Vec3d color;

    @ModifyArgs(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;"))
    private void ean_captureCloudRenderLocals(Args args) {
        bufferBuilder  = args.get(0);
        x = args.get(1);
        y = args.get(2);
        z = args.get(3);
        color = args.get(4);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", ordinal = 1))
    private void ean_renderClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f positionMatrix, float tickDelta, double x, double y, double z, Operation<Void> original){
        if (CloudConfig.getOrCreateInstance().isUseEanClouds())
            EanCloudRenderBehaviour.ean_renderClouds(instance, matrices, positionMatrix, tickDelta, x, y, z);
        else
            original.call(instance, matrices, positionMatrix, tickDelta, x, y, z);
    }
}