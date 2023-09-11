package com.github.Soulphur0.mixin;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {

//    @ModifyVariable(method="getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), ordinal = 0, argsOnly = true)
//    private boolean ean_letsGetThisOverWithShallWe(boolean value){
//        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
//            return false;
//        else
//            return value;
//    }

//    @WrapOperation(method="getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkManager;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"))
//    private Chunk ean_letsGetThisOverWithShallWe(ChunkManager chunkManager, int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create, Operation<Chunk> original){
//        PlayerEntity player = ((World)(Object)this).getClosestPlayer(chunkX, WorldRenderingConfig.getOrCreateInstance().getUnloadingHeight(), chunkZ, 9999.0, false);
//
//        if (player != null && !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
//            return original.call(chunkManager, chunkX, chunkZ, leastStatus, false);
//        else
//            return original.call(chunkManager, chunkX, chunkZ, leastStatus, create);
//    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void ean_test11(BlockPos pos, CallbackInfoReturnable<BlockState> cir){
        PlayerEntity player = ((World)(Object)this).getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8.0, false);

        //if (player != null && !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
        //  cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());

        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void ean_test12(BlockPos pos, CallbackInfoReturnable<FluidState> cir){
        PlayerEntity player = ((World)(Object)this).getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8.0, false);

//        if (player != null && !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
//            cir.setReturnValue(Fluids.EMPTY.getDefaultState());

        if(!FlightConfig.getOrCreateInstance().isSneakingRealignsPitch())
            cir.setReturnValue(Fluids.EMPTY.getDefaultState());
    }
}
