package com.github.Soulphur0.registries;

import com.github.Soulphur0.networking.client.EanClientPacketDispatcher;
import com.github.Soulphur0.networking.server.EanServerPacketDispatcher;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class EanNetworkingRegistry {

    // . S2C packet IDs
    public static final Identifier CONFIG_SYNC_PACKET_ID = new Identifier("ean", "sync_config");
    public static final Identifier CLIENT_CONFIG_PACKET_ID = new Identifier("ean", "client_config");

    // . C2S packet IDs
    public static final Identifier CLIENT_CHUNK_LOADING_ID = new Identifier("ean", "client_chunk_loading");

    public static void registerEanServerReceivers(){
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_CHUNK_LOADING_ID, new EanServerPacketDispatcher());
    }

    public static void registerEanClientReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(CONFIG_SYNC_PACKET_ID, new EanClientPacketDispatcher());
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_CONFIG_PACKET_ID, new EanClientPacketDispatcher());
    }
}
