package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method ="tick", at = @At("HEAD"))
    private void eanLastHopes(CallbackInfo ci){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            ((PlayerEntity)(Object)this).noClip = true;
    }
}