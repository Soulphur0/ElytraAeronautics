package com.github.Soulphur0;

import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.clothConfig.EanConfig;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	@Override
	public void onInitialize() {
		// ? Check for ClothConfig, if not present, create config file manually.
		// Register config class as config screen.
		AutoConfig.register(EanConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

		// Register save config listener, this will load the config screen data to memory and write it to storage.
		AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener(((configHolder, eanConfig) -> {
			FlightConfig.readConfig(eanConfig.getFlightConfigScreen());
			CloudConfig.updateConfig(eanConfig.getCloudConfigScreen());
			return ActionResult.PASS;
		}));
//		if (DependencyChecker.checkForClothConfig()){
//
//		} else {
//
//		}

		EanCommands.register();
		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}