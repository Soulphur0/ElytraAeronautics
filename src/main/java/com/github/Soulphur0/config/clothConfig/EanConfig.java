package com.github.Soulphur0.config.clothConfig;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "ElytraAeronautics")
public class EanConfig extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.TransitiveObject
    FlightConfigScreen flightConfigScreen = new FlightConfigScreen();

    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.Gui.TransitiveObject
    CloudConfigScreen cloudConfigScreen = new CloudConfigScreen();

    // $ GETTERS
    public FlightConfigScreen getFlightConfigScreen() {
        return flightConfigScreen;
    }

    public CloudConfigScreen getCloudConfigScreen() {
        return cloudConfigScreen;
    }

    public enum Info {
        INFO
    }
}
