package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanClientPlayerData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    // _ World class' getBlockState() bypass.
    // ? Called in ClientWorld randomBlockDisplayTick().
    // ¿ Gets random blocks across the world to tick visual effects.
    @WrapOperation(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState ean_conditionGetBlockState(ClientWorld instance, BlockPos pos, Operation<BlockState> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance, pos);
        else
            return Blocks.VOID_AIR.getDefaultState();
    }

    // _ World class' getFluidState() bypass.
    // ? Called in ClientWorld randomBlockDisplayTick().
    // ¿ Gets random fluids across the world to tick visual effects.
    @WrapOperation(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState ean_conditionGetFluidState(ClientWorld instance, BlockPos pos, Operation<FluidState> original){
        if (EanClientPlayerData.hasChunkLoadingAbility())
            return original.call(instance, pos);
        else
            return Fluids.EMPTY.getDefaultState();
    }
}