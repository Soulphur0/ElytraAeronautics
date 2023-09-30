package com.github.Soulphur0.registries;

import com.github.Soulphur0.behaviour.server.EanRocketBoostBehaviour;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import com.github.Soulphur0.networking.EanPlayerDataCache;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;

public class EanEventRegistry {

    public static void registerEanEvents(){
        registerConfigSyncEvent();
        registerRocketBoostEvent();
    }

    // = On world/server join, sync the config, on dedicated servers reading from disk is not needed.
    private static void registerConfigSyncEvent(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
            ServerPlayerEntity player = (ServerPlayerEntity) handler.player;

            if (server.isDedicated())
                EanServerPacketSender.syncClientConfigWithServer(player);
            else {
                FlightConfig.readFromDisk();
                WorldRenderingConfig.readFromDisk();
                EanServerPacketSender.syncClientConfigWithServer(player);
            }
        });
    }

    // = On item usage, if the player does not load chunks and uses a firework rocket, rocket boost them.
    // ; There's no need to elytra flight check since the player can only not load chunks while elytra flying.
    private static void registerRocketBoostEvent(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack handStack = player.getMainHandStack();

            if (!EanPlayerDataCache.canPlayerLoadChunks(player.getUuid()) && handStack.getItem() == Items.FIREWORK_ROCKET){
                player.setVelocity(EanRocketBoostBehaviour.calcFireworkRocketBoost(player));
                if (!player.isCreative() && !player.isSpectator())
                    handStack.setCount(handStack.getCount() - 1);
                return TypedActionResult.success(handStack);
            }

            return TypedActionResult.pass(handStack);
        });
    }
}