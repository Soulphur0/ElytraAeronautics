package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRendererRegionBuilder.class)
public class ChunkRendererRegionBuilderMixin {

    @Inject(method = "isEmptyBetween", at = @At("HEAD"), cancellable = true)
    private static void ean_returnEmpty(BlockPos startPos, BlockPos endPos, int offsetX, int offsetZ, ChunkRendererRegionBuilder.ClientChunk[][] chunks, CallbackInfoReturnable<Boolean> cir){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            cir.setReturnValue(true);

    }

    @Inject(method = "build", at = @At("HEAD"), cancellable = true)
    private void ean_returnEmpty(World world, BlockPos startPos, BlockPos endPos, int offset, CallbackInfoReturnable<ChunkRendererRegion> cir){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            cir.setReturnValue(null);

    }
}
