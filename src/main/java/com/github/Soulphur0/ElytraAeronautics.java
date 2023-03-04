package com.github.Soulphur0;

import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.registries.ElytraAeronauticsCommands;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElytraAeronautics implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ElytraAeronautics");
	public static boolean readConfigFileCue_WorldRendererMixin = true;
	public static boolean readConfigFileCue_LivingEntityMixin = true;

	@Override
	public void onInitialize() {
		AutoConfig.register(EanConfig.class, GsonConfigSerializer::new);
		ElytraAeronauticsCommands.register();

		LOGGER.info("Elytra Aeronautics initialized! Have a good flight!");
	}
}
