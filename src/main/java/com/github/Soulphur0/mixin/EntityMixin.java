package com.github.Soulphur0.mixin;

import com.github.Soulphur0.utility.EanPlayerDataCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Entity.class, priority = 500)
public class EntityMixin {

    // _ World class' getBlockState() bypass.
    // ? Called in Entity move().
    // ¿ Bypasses most of the movement calls and therefore getBlockState() calls that aren't made in spectator mode either.
    @ModifyExpressionValue(method ="move", at = @At(value = "FIELD", target ="Lnet/minecraft/entity/Entity;noClip:Z"))
    private boolean ean_bypassMovementCalls(boolean original){
        Entity entity = ((Entity)(Object)this);

        if (entity.isPlayer()){
            PlayerEntity player = (PlayerEntity) entity;
            return original || !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        } else
            return original;
    }

    // _ World class' getBlockState() bypass.
    @ModifyExpressionValue(method ="updateMovementInFluid", at = @At(value = "INVOKE", target ="Lnet/minecraft/entity/Entity;isRegionUnloaded()Z"))
    private boolean ean_bypassMovementInFluidCalls(boolean original){
        Entity entity = ((Entity)(Object)this);

        if (entity.isPlayer()){
            PlayerEntity player = (PlayerEntity) entity;
            return original || !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        } else
            return original;
    }

    // _ World class' getFluidState() bypass.
    @WrapOperation(method ="updateSubmergedInWaterState", at = @At(value ="INVOKE", target ="Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState ean_bypassGetFluidState(World instance, BlockPos pos, Operation<FluidState> original){
        Entity entity = ((Entity)(Object)this);

        if (entity.isPlayer()){
            PlayerEntity player = (PlayerEntity) entity;
            if (!EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
                return Fluids.EMPTY.getDefaultState();
            else
                return original.call(instance, pos);
        } else
            return original.call(instance, pos);
    }
}
