package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {

    @ModifyVariable(method="getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), ordinal = 0)
    private boolean ean_letsGetThisOverWithShallWe(boolean value){
        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            return false;
        else
            return value;
    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void ean_test11(BlockPos pos, CallbackInfoReturnable<BlockState> cir){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch()) {
            cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());
        }
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void ean_test12(BlockPos pos, CallbackInfoReturnable<FluidState> cir){
        if (!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch()) {
            cir.setReturnValue(Fluids.EMPTY.getDefaultState());
        }
    }
}
