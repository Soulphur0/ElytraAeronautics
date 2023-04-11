package com.github.Soulphur0;

import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.clothConfig.EanConfig;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.integration.DependencyChecker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	// TODO: This will be a future conflict; ConfigSystemRefactorization started in 16/03/23
	// Continued 17/03/23
	@Override
	public void onInitialize() {
		// ? Check for ClothConfig, and register config screen and listeners.
		if (DependencyChecker.checkForClothConfig()){
			// Register config class as config screen.
			AutoConfig.register(EanConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

			// Register save config listener, this will load the config screen data to memory and write it to storage.
			AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener(((configHolder, eanConfig) -> {
				FlightConfig.updateConfig(eanConfig.getFlightConfigScreen());
				CloudConfig.updateConfig(eanConfig.getCloudConfigScreen());
				return ActionResult.PASS;
			}));

			// Store config screens in each singleton, in order to update the config screen in case values were changed via command.
			FlightConfig.configScreen = AutoConfig.getConfigHolder(EanConfig.class).getConfig().getFlightConfigScreen();
			CloudConfig.configScreen = AutoConfig.getConfigHolder(EanConfig.class).getConfig().getCloudConfigScreen();
		}

		// ? Read data saved in disk directly to config instance.
		FlightConfig.readFromDisk();
		CloudConfig.readFromDisk();

		// ? Register config command.
		EanCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}