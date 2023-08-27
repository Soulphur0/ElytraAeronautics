package com.github.Soulphur0.networking.server;

import net.minecraft.network.PacketByteBuf;

/**
 *  Utility class used to easily handle writing and reading Elytra Aeronautics' config custom packets.
 * @see EanServerSettingsPacket
 * */
public class EanServerSettingsPacketSerializer {
    public static void write(PacketByteBuf buf, EanServerSettingsPacket packet) {
        packet.write(buf);
    }

    public static EanServerSettingsPacket read(PacketByteBuf buf) {
        return EanServerSettingsPacket.read(buf);
    }
}
