package com.github.Soulphur0.config;

import com.github.Soulphur0.config.singletons.FlightConfig;
import net.minecraft.network.PacketByteBuf;

/**
 * Class for EanServerSettings objects.<br><br>
 * This class, unlike the rest of config classes, is not a singleton, but rather an object that is instantiated whenever server's settings need to be packed to be sent to a client.<br><br>
 * @see com.github.Soulphur0.networking.EanServerSettingsPacket
 * @see com.github.Soulphur0.networking.EanServerSettingsPacketSerializer
 * */
public class EanServerSettings {

    public static boolean settingsChanged = false;

    private FlightConfig flightConfigInstance;

    // $ CONSTRUCTORS

    // ? Without parameters.
    // ¿ Used in the EanNetworkingUtilities class, syncClientConfigWithServer() method: packs the server's config instances into this object to send it to the client.
    public EanServerSettings(){
        flightConfigInstance = FlightConfig.getOrCreateInstance();
    }

    // ? With parameters.
    // ¿ Used in this class createFromBuffer() method: packs the received server settings into this object to override the client's settings.
    public EanServerSettings(FlightConfig flightConfig){
        flightConfigInstance = flightConfig;
    }

    // . NETWORKING

    // ? Write to the packet buff all server-dependant config values sequentially.
    public void writeToBuffer(PacketByteBuf buf){
        // + Flight settings
        buf.writeBoolean(flightConfigInstance.isAltitudeDeterminesSpeed());
        buf.writeDouble(flightConfigInstance.getMinSpeed());
        buf.writeDouble(flightConfigInstance.getMaxSpeed());
        buf.writeDouble(flightConfigInstance.getMinHeight());
        buf.writeDouble(flightConfigInstance.getMaxHeight());
        buf.writeBoolean(flightConfigInstance.isSneakingRealignsPitch());
        buf.writeFloat(flightConfigInstance.getRealignAngle());
        buf.writeFloat(flightConfigInstance.getRealignRate());
    }

    // ? Read from the packet buff all server-dependant config values sequentially.
    public static EanServerSettings createFromBuffer(PacketByteBuf buf){
        // + Flight settings
        boolean altitudeDeterminesSpeed = buf.readBoolean();
        double minSpeed = buf.readDouble();
        double maxSpeed = buf.readDouble();
        double minHeight = buf.readDouble();
        double maxHeight = buf.readDouble();
        boolean sneakingRealignsPitch = buf.readBoolean();
        float realignAngle = buf.readFloat();
        float realignRate = buf.readFloat();

        // = Config instances with server values
        FlightConfig flightConfig = new FlightConfig(altitudeDeterminesSpeed, minSpeed, maxSpeed, minHeight, maxHeight, sneakingRealignsPitch, realignAngle, realignRate);

        return new EanServerSettings(flightConfig);
    }

    // $ GETTERS/SETTERS
    public FlightConfig getFlightConfigInstance() {
        return flightConfigInstance;
    }

    public void setFlightConfigInstance(FlightConfig flightConfigInstance) {
        this.flightConfigInstance = flightConfigInstance;
    }
}
