package com.github.Soulphur0;

import com.github.Soulphur0.config.command.EanCommand;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import com.github.Soulphur0.networking.server.EanServerPacketDispatcher;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	// . S2C packets
	public static final Identifier CONFIG_SYNC_PACKET_ID = new Identifier("ean", "sync_config");
	public static final Identifier CLIENT_CONFIG_PACKET_ID = new Identifier("ean", "client_config");

	// . C2S packets
	public static final Identifier CLIENT_CHUNK_LOADING_ID = new Identifier("ean", "client_chunk_loading");

	@Override
	public void onInitialize() {
		// ? Read the config data saved in disk directly to config instance upon initialization.
		FlightConfig.readFromDisk();
		WorldRenderingConfig.readFromDisk();

		// ? Register config command.
		EanCommand.register();

		// ? Register event handler.
		// ¿ On world/server join, sync the config, on dedicated servers reading from disk is not needed.
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

		// . Register server receivers
		ServerPlayNetworking.registerGlobalReceiver(ElytraAeronautics.CLIENT_CHUNK_LOADING_ID, new EanServerPacketDispatcher());

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}