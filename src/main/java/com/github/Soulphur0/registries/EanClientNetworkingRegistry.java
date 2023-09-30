package com.github.Soulphur0.registries;

import com.github.Soulphur0.networking.client.EanClientPacketDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import static com.github.Soulphur0.registries.EanNetworkingRegistry.CLIENT_CONFIG_PACKET_ID;
import static com.github.Soulphur0.registries.EanNetworkingRegistry.CONFIG_SYNC_PACKET_ID;

@Environment(EnvType.CLIENT)
public class EanClientNetworkingRegistry {

    public static void registerEanClientReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(CONFIG_SYNC_PACKET_ID, new EanClientPacketDispatcher());
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_CONFIG_PACKET_ID, new EanClientPacketDispatcher());
    }
}
