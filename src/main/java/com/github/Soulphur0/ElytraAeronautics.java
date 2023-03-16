package com.github.Soulphur0;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.integration.DependencyChecker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");

	@Override
	public void onInitialize() {
		// ? Check for ClothConfig, if not present, create config file manually.
		if (DependencyChecker.checkForClothConfig()){
			// Register config class as config screen
			AutoConfig.register(EanConfig.class, GsonConfigSerializer::new);

			// Register flight config listener
			AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener(((configHolder, eanConfig) -> {
				FlightConfig.readConfig(eanConfig);
				return ActionResult.PASS;
			}));

			// Register cloud config listener
			AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener((configHolder, eanConfig)->{
				CloudConfig.CloudLayer.readConfig(eanConfig);
				EanCloudRenderBehaviour.configUpdated = true;
				return ActionResult.PASS;
			});
		} else {

		}
		EanCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}