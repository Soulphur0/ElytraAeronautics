package com.github.Soulphur0;

import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.networking.EanNetworkingUtilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");
	public static final Identifier CONFIG_SYNC_PACKET_ID = new Identifier("ean", "sync_config");

	@Override
	public void onInitialize() {

		// ? On world/server join, sync the config.
		// ¿ On dedicated servers reading from disk is not needed.
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
			ServerPlayerEntity player = (ServerPlayerEntity) handler.player;

			if (server.isDedicated())
				EanNetworkingUtilities.syncClientConfigWithServer(player);
			else {
				FlightConfig.readFromDisk();
				EanNetworkingUtilities.syncClientConfigWithServer(player);
			}
		});

		// ? Read the config data saved in disk directly to config instance upon initialization.
		FlightConfig.readFromDisk();
		CloudConfig.readFromDisk();

		// ? Register config command.
		EanCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}