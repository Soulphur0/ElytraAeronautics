package com.github.Soulphur0.networking.client;

import com.github.Soulphur0.config.EanClientSettings;
import net.minecraft.network.PacketByteBuf;

/**
 * Elytra Aeronautics' custom packet class, it holds a EanClientSettings instance with a client config setting.<br><br>
 * Reading and writing calls are done by the EanClientSettingsPacketSerializer class.<br><br>
 * The logic of packing and unpacking the settings themselves is done within the EanClientSettings class.<br><br>
 * @see EanClientSettingsPacketSerializer
 * @see EanClientSettings
 * */
public class EanClientSettingsPacket {
    private final EanClientSettings clientSettings;

    public EanClientSettingsPacket(EanClientSettings eanClientSettings){
        this.clientSettings = eanClientSettings;
    }

    public void write(PacketByteBuf buf){ clientSettings.writeToBuffer(buf);}

    public static EanClientSettingsPacket read(PacketByteBuf buf){
        EanClientSettings clientSettings = EanClientSettings.createFromBuffer(buf);

        return new EanClientSettingsPacket(clientSettings);
    }

    public EanClientSettings getClientSettings() { return clientSettings; }
}
