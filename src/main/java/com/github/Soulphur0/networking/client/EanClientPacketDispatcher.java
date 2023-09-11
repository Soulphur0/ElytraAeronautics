package com.github.Soulphur0.networking.client;

import com.github.Soulphur0.config.EanServerSettings;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import com.github.Soulphur0.config.updaters.CloudConfigUpdater;
import com.github.Soulphur0.networking.server.EanServerSettingsPacket;
import com.github.Soulphur0.networking.server.EanServerSettingsPacketSerializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Class that receives incoming packets from the server and dispatches them into different handlers based on the packet's first byte, which determines its packet type.<br><br>
 * Right now, its use mainly revolve around synchronizing settings, it may serve other purposes further on.<br><br>
 * */
public class EanClientPacketDispatcher implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int packetType = buf.readInt();

        switch (packetType) {
            case 0 -> {
                EanServerSettingsPacket packet = EanServerSettingsPacketSerializer.read(buf);
                EanServerSettings serverSettings = packet.getServerSettings();
                FlightConfig.updateClientSettings(serverSettings.getFlightConfigInstance());
                WorldRenderingConfig.updateClientSettings(serverSettings.getWorldRenderingConfigInstance());
            }
            case 1 -> processClientSettings(client, buf.readString(), buf);
        }
    }

    // ? Based on the received client setting's category, a method for its related config class updates the settings with the passed values.
    private void processClientSettings(MinecraftClient client, String settingCategory, PacketByteBuf settingAttributes){
        switch (settingCategory){
            case "generalCloudConfig" -> CloudConfigUpdater.updateGeneralConfig(client, settingAttributes);
            case "cloudLayerConfig" -> CloudConfigUpdater.updateCloudLayerConfig(client, settingAttributes);
        }
    }
}
