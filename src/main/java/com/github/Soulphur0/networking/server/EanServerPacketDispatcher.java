package com.github.Soulphur0.networking.server;

import com.github.Soulphur0.utility.EanPlayerDataCache;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class EanServerPacketDispatcher implements ServerPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int packetType = buf.readInt();

        // Yeah, the switch is unnecessary, but I'll need it in the future, so I'll leave it like this.
        switch (packetType){
            case 0 -> EanPlayerDataCache.setOrUpdateCanPlayerLoadChunks(buf);
        }
    }
}
