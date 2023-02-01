package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    // $ Capture camera altitude of the World Renderer.
    // * Before it calls the second phase of cloud rendering where it is modified to be the difference between the camera's altitude and the world's cloud layer height.
    double cameraAltitude;

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void ean_captureCameraAltitude(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci){
        cameraAltitude = e;
    }

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

    // $ Custom render clouds with captured arguments by default.
    @ModifyReturnValue(method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At("RETURN"))
    private BufferBuilder.BuiltBuffer ean_renderClouds(BufferBuilder.BuiltBuffer original) {
        return EanCloudRenderBehaviour.ean_renderCloudLayers(bufferBuilder, cameraAltitude, x, y, z, color);
    }
}