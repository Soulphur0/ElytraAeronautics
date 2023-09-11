package com.github.Soulphur0;

import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.networking.client.EanClientPacketDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ElytraAeronauticsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(){
        ClientPlayNetworking.registerGlobalReceiver(ElytraAeronautics.CONFIG_SYNC_PACKET_ID, new EanClientPacketDispatcher());
        ClientPlayNetworking.registerGlobalReceiver(ElytraAeronautics.CLIENT_CONFIG_PACKET_ID, new EanClientPacketDispatcher());

        CloudConfig.readFromDisk();
    }
}
