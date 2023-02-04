package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.EanConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    @Shadow public abstract void tick();

    // $ Capture camera of the World Renderer.
    Camera camera;
    @Inject(method="render", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void ean_captureCameraObject(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci){
        this.camera = camera;
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

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", ordinal = 1))
    private void ean_renderClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f positionMatrix, float tickDelta, double x, double y, double z, Operation<Void> original){
        EanConfig config = AutoConfig.getConfigHolder(EanConfig.class).getConfig();
        if (config.numberOfLayers > 0){
            EanCloudRenderBehaviour.ean_renderClouds(instance, matrices, positionMatrix, tickDelta, x, y, z);
        }
    }

//    @WrapOperation(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;"))
//    private BufferBuilder.BuiltBuffer ean_renderClouds(WorldRenderer instance, BufferBuilder builder, double x, double y, double z, Vec3d color, Operation<BufferBuilder.BuiltBuffer> original){
//
//    }
//
//    // $ Custom render clouds with captured arguments by default.
//    @ModifyReturnValue(method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At("RETURN"))
//    private BufferBuilder.BuiltBuffer ean_renderCloudsOld(BufferBuilder.BuiltBuffer original) {
//        return EanCloudRenderBehaviour.ean_renderCloudLayers(bufferBuilder, camera.getPos().getY(), x, y, z, color);
//    }
}