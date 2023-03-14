package com.github.Soulphur0;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.behaviour.EanFlightBehaviour;
import com.github.Soulphur0.config.EanCommands;
import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.config.singletons.CloudLayer;
import com.github.Soulphur0.config.singletons.ElytraFlight;
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
		AutoConfig.register(EanConfig.class, GsonConfigSerializer::new);
		AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener((configHolder, eanConfig)->{
			CloudLayer.generateCloudLayers(eanConfig);
			EanCloudRenderBehaviour.configUpdated = true;
			return ActionResult.PASS;
		});
		AutoConfig.getConfigHolder(EanConfig.class).registerSaveListener(((configHolder, eanConfig) -> {
			ElytraFlight.refresh(eanConfig);
			return ActionResult.PASS;
		}));
		EanCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}
