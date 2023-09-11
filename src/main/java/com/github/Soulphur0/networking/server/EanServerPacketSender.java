package com.github.Soulphur0.networking.server;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.EanClientSettings;
import com.github.Soulphur0.config.EanServerSettings;
import com.github.Soulphur0.networking.client.EanClientSettingsPacket;
import com.github.Soulphur0.networking.client.EanClientSettingsPacketSerializer;
import com.mojang.brigadier.context.CommandContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Class that holds utility method for networking.<br><br>
 * Its main purpose is to hold static methods with all necessary sends to the client.<br><br>
 * */
public class EanServerPacketSender {

    // : SERVER CONFIG ---------------------------------------------------------------------------------------------------

    // ? Sync all clients' config with the server.
    // ¿ Used when the server config is changed by an operator.
    public static void syncAllClientsConfigWithServer(MinecraftServer server){

        // + Write server settings into custom packet.
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        EanServerSettingsPacketSerializer.write(buf, new EanServerSettingsPacket(new EanServerSettings()));

        // + Send sync order to all connected clients.
        for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)){
            ServerPlayNetworking.send(serverPlayer, ElytraAeronautics.CONFIG_SYNC_PACKET_ID, buf);
        }
    }

    // ? Sync a single client's config with the server.
    // ¿ Used when a player joins the server.
    public static void syncClientConfigWithServer(PlayerEntity user){
        if(user.getWorld().isClient()) return;

        // + Write server settings into custom packet.
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        EanServerSettingsPacketSerializer.write(buf, new EanServerSettingsPacket(new EanServerSettings()));

        // + Send sync order to the joined player.
        ServerPlayNetworking.send((ServerPlayerEntity) user, ElytraAeronautics.CONFIG_SYNC_PACKET_ID, buf);
    }

    // : CLIENT CONFIG ---------------------------------------------------------------------------------------------------

    // ? Send config written in command to the client.
    // ¿ Since the config command is server side, it is needed to send a packet to the client in order to update client-related configuration.
    // ! A whole custom packet is not needed because client config will always only need to get one value read at the same time, while server config needs all values to be read at once when joining a server.
    public static void sendUpdatedClientConfig(EanClientSettings setting, CommandContext<ServerCommandSource> context){
        if (context.getSource().getWorld().isClient() || context.getSource().getPlayer() == null) return;

        // + Write new settings into custom packet.
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        EanClientSettingsPacketSerializer.write(buf, new EanClientSettingsPacket(setting));

        // + Send packet to the client.
        ServerPlayNetworking.send(context.getSource().getPlayer(), ElytraAeronautics.CLIENT_CONFIG_PACKET_ID, buf);
    }
}