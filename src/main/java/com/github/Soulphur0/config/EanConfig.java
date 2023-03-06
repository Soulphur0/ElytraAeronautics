package com.github.Soulphur0.config;

import com.github.Soulphur0.config.cloudlayer.CloudTypes;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ean")
public class EanConfig implements ConfigData {

    // : Metadata
    @ConfigEntry.Category(value ="elytra_flight_settings")
    @ConfigEntry.Gui.Excluded
    public boolean generateDefaultPreset = true;

    // : Elytra flight fields
    @ConfigEntry.Category(value ="elytra_flight_settings")
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

    // : Cloud layers fields
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value ="cloud_settings")
    public int numberOfLayers = 2;
    @ConfigEntry.Category(value ="cloud_settings")
    public float distanceBetweenLayers = 250.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    public float firstLayerAltitude = 192.0F;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category(value ="cloud_settings")
    public CloudTypes cloudType = CloudTypes.LOD;
    @ConfigEntry.Category(value ="cloud_settings")
    public float verticalRenderDistance = 1000;
    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.BoundedDiscrete(min = 2, max = 128)
    public int horizontalRenderDistance = 16;
    @ConfigEntry.Category(value ="cloud_settings")
    public float lodRenderDistance = 50.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    public boolean useSmoothLods = true;
    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.Gui.PrefixText
    public float cloudThickness =  4.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    public float cloudSpeed = 1.0F;
}
