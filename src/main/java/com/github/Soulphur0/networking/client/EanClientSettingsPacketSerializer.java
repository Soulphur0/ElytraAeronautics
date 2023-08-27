package com.github.Soulphur0.networking.client;

import net.minecraft.network.PacketByteBuf;

/**
 *  Utility class used to easily handle writing and reading Elytra Aeronautics' config custom packets.
 * @see EanClientSettingsPacket
 * */
public class EanClientSettingsPacketSerializer {
    public static void write(PacketByteBuf buf, EanClientSettingsPacket packet) {
        packet.write(buf);
    }

    public static EanClientSettingsPacket read(PacketByteBuf buf) {
        return EanClientSettingsPacket.read(buf);
    }
}
