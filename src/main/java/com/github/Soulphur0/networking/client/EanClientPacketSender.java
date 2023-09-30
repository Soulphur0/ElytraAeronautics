package com.github.Soulphur0.networking.client;

import com.github.Soulphur0.registries.EanNetworkingRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class EanClientPacketSender {

    // ? Sends whether the player's should generate chunks to the server.
    // ¿ Used in the mixin entries.
    public static void sendPlayerChunkLoadingAbility(UUID playerUuid, boolean canLoadChunks){

        // +  Write data into packet.
        // * Packet type C2S identifier, player UUID and chunk loading capability.
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(0);
        buf.writeUuid(playerUuid);
        buf.writeBoolean(canLoadChunks);

        // + Send packet to the server.
        ClientPlayNetworking.send(EanNetworkingRegistry.CLIENT_CHUNK_LOADING_ID, buf);
    }
}
