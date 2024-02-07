package com.github.Soulphur0.mixin;

import com.github.Soulphur0.behaviour.server.EanFlightBehaviour;
import com.github.Soulphur0.behaviour.server.EanPitchRealignmentBehaviour;
import com.github.Soulphur0.networking.EanPlayerDataCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world){
        super(entityType,world);
    }

    // $ Flight speed injection point.
    @ModifyArg(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 6))
    private Vec3d ean_modifyVelocity(Vec3d original){
        return EanFlightBehaviour.ean_flightBehaviour(((LivingEntity)(Object)this), original);
    }

    // $ Pitch realignment injection point.
    @Inject(method="travel", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;")) // This isnt the exact same injection point, but it should be close enough that it *shouldn't* affect anything
    private void ean_realignPitch(Vec3d movementInput, CallbackInfo ci){
        EanPitchRealignmentBehaviour.realignPitch(((LivingEntity)(Object)this));
    }

    // _ World class' getBlockState() bypass.
    @ModifyExpressionValue(method ="isClimbing", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSpectator()Z"))
    private boolean ean_returnIsClimbing(boolean original){
        LivingEntity livingEntity = ((LivingEntity)(Object)this);

        if (livingEntity.isPlayer()){
            PlayerEntity player = (PlayerEntity) livingEntity;
            return original || !EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        } else
            return original;
    }

    // _ World class' getBlockState() bypass.
    @WrapWithCondition(method ="tickMovement", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addPowderSnowSlowIfNeeded()V"))
    private boolean ean_bypassAddPowderSnowSlowIfNeeded(LivingEntity instance) {
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (livingEntity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            return EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        } else
            return true;
    }

    // _ World class' getBlockState() bypass.
    @ModifyExpressionValue(method ="baseTick", at = @At(value ="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isAlive()Z", ordinal = 0))
    private boolean ean_addConditionToAmbientDamageEffectsCalculations(boolean original){
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (livingEntity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            return original && EanPlayerDataCache.canPlayerLoadChunks(player.getUuid());
        } else
            return original;
    }

    // _ World class' getBlockState() bypass.
    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isWet()Z"))
    private boolean ean_skipIsWet(LivingEntity instance, Operation<Boolean> original){
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (livingEntity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (!EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
                return false;
            else return original.call(instance);
        }
        return original.call(instance);
    }

    // _ World class' getFluidState() bypass.
    @WrapOperation(method ="travel", at = @At(value ="INVOKE", target ="Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    private FluidState ean_bypassGetFluidState(World instance, BlockPos pos, Operation<FluidState> original){
        LivingEntity livingEntity = ((LivingEntity) (Object) this);

        if (livingEntity.isPlayer()) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (!EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()))
                return Fluids.EMPTY.getDefaultState();
            else return original.call(instance, pos);
        }
        return original.call(instance, pos);
    }
}
