package com.github.Soulphur0;

import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	@Override
	public void onInitialize() {
		// ? Read data saved in disk directly to config instance.
		FlightConfig.readFromDisk();
		CloudConfig.readFromDisk();

		// ? Register config command.
		EanCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}