package com.github.Soulphur0.mixin;

import com.github.Soulphur0.networking.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(BubbleColumnSoundPlayer.class)
public class BubbleColumnSoundPlayerMixin {

    // _ World class' getBlockState() bypass.
    // ? Called in ClientPlayerEntity tick().
    // ¿ Ticks this ClientPlayerTickable.
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/World;getStatesInBoxIfLoaded(Lnet/minecraft/util/math/Box;)Ljava/util/stream/Stream;"))
    private Stream<BlockState> ean_bypassTickCalculations(World instance, Box box, Operation<Stream<BlockState>> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance, box);
        else
            return Stream.empty();
    }
}
