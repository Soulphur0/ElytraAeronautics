package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    // _ World class' getBlockState() bypass.
    // ? Called in DebugHud render().
    // ¿ Gets the block the player is looking at for the F3 screen.
    @WrapOperation(method ="render", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;", ordinal = 0))
    private HitResult ean_missBlockRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids, Operation<HitResult> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance, maxDistance, tickDelta, includeFluids);
        else
            return BlockHitResult.createMissed(Vec3d.ZERO, Direction.UP, BlockPos.ORIGIN);
    }
}
