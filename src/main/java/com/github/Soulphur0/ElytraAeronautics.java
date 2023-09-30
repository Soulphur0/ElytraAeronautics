package com.github.Soulphur0;

import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import com.github.Soulphur0.registries.EanCommandRegistry;
import com.github.Soulphur0.registries.EanEventRegistry;
import com.github.Soulphur0.registries.EanNetworkingRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	@Override
	public void onInitialize() {
		// ? Read server config data saved in disk into config instance.
		FlightConfig.readFromDisk();
		WorldRenderingConfig.readFromDisk();

		// ? Call registries.
		EanCommandRegistry.registerEanCommands();
		EanEventRegistry.registerEanEvents();
		EanNetworkingRegistry.registerEanServerReceivers();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}