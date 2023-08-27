package com.github.Soulphur0;

import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.networking.client.EanClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ElytraAeronauticsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(){
        ClientPlayNetworking.registerGlobalReceiver(ElytraAeronautics.CONFIG_SYNC_PACKET_ID, new EanClientPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ElytraAeronautics.CLIENT_CONFIG_PACKET_ID, new EanClientPacketHandler());

        CloudConfig.readFromDisk();
    }
}
