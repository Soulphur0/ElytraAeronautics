package com.github.Soulphur0.networking.server;

import com.github.Soulphur0.config.EanServerSettings;
import net.minecraft.network.PacketByteBuf;

/**
 * Elytra Aeronautics' custom packet class, it holds a EanServerSettings instance with all server-dependant config settings.<br><br>
 * Reading and writing calls are done by the EanServerSettingsPacketSerializer class.<br><br>
 * The logic of packing and unpacking the settings themselves is done within the EanServerSettings class.<br><br>
 * @see EanServerSettingsPacketSerializer
 * @see EanServerSettings
 * */
public class EanServerSettingsPacket {
    private final EanServerSettings serverSettings;

    public EanServerSettingsPacket(EanServerSettings eanServerSettings){
        this.serverSettings = eanServerSettings;
    }

    public void write(PacketByteBuf buf){
        serverSettings.writeToBuffer(buf);
    }

    public static EanServerSettingsPacket read(PacketByteBuf buf){
        EanServerSettings serverSettings = EanServerSettings.createFromBuffer(buf);

        return new EanServerSettingsPacket(serverSettings);
    }

    public EanServerSettings getServerSettings(){
        return serverSettings;
    }
}
