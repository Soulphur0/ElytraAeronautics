package com.github.Soulphur0.mixin;

import net.minecraft.client.render.Camera;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Final
    @Shadow @Mutable
    private Vector3f horizontalPlane;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void ean_test(CallbackInfo ci){
        this.horizontalPlane = new Vector3f(0.0F, 2.0F, 0.0F);
    }
}
