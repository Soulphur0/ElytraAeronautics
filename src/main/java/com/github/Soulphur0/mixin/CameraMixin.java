package com.github.Soulphur0.mixin;

import com.github.Soulphur0.networking.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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

    // _ World class' getBlockState() bypass.
    // ? Called in GameRenderer getFov().
    // ¿ Gets submersion type for fluids in order to change the FOV.
    @ModifyExpressionValue(method ="getSubmersionType", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/Camera;ready:Z"))
    private boolean ean_bypassCameraSubmersionType(boolean original) {
            return original || !EanClientPlayerData.hasChunkLoadingAbility();
    }
}
