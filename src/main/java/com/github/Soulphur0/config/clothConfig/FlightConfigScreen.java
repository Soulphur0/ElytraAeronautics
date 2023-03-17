package com.github.Soulphur0.config.clothConfig;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class FlightConfigScreen implements ConfigData {
    public FlightConfigScreen() {

    }

    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Excluded
    public boolean fresh = true;
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean altitudeDeterminesSpeed = true;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public double minSpeed = 30.35;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip
    public double maxSpeed = 257.22;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip
    public double minHeight = 250.0;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip
    public double maxHeight = 1000.0;

    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip
    public boolean sneakingRealignsPitch = true;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip
    public float realignAngle = 0.0F;
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public float realignRate = 0.1F;

}
