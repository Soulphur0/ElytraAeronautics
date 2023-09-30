package com.github.Soulphur0;

import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.registries.EanClientNetworkingRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ElytraAeronauticsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(){
        // ? Read client config data saved in disk into config instance.
        CloudConfig.readFromDisk();

        // ? Call registries.
        EanClientNetworkingRegistry.registerEanClientReceivers();
    }
}
