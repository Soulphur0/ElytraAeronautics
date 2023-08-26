package com.github.Soulphur0.networking;

import com.github.Soulphur0.config.EanServerSettings;
import com.github.Soulphur0.config.singletons.FlightConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Class that handles incoming packets from the server.<br><br>
 * Right now, its use mainly revolve around synchronizing settings, it may serve other purposes further on.<br><br>
 * */
public class EanClientPacketHandler implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        EanServerSettingsPacket packet = EanServerSettingsPacketSerializer.read(buf);

        EanServerSettings serverSettings = packet.getServerSettings();

        FlightConfig.updateClientSettings(serverSettings.getFlightConfigInstance());
    }
}
