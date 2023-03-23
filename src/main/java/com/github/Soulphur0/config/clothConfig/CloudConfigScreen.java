package com.github.Soulphur0.config.clothConfig;

import com.github.Soulphur0.config.singletons.CloudConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "cloud_settings")
public class CloudConfigScreen implements ConfigData {
    public CloudConfigScreen() {
    }

    @ConfigEntry.Category(value ="cloud_settings")
    public boolean useEanClouds = true;
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Category(value ="cloud_settings")
    public int numberOfLayers = 2;
    @ConfigEntry.Category(value ="cloud_settings")
    public float firstLayerAltitude = 192.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    public float distanceBetweenLayers = 25.0F;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category(value ="cloud_settings")
    public CloudConfig.CloudTypes cloudType = CloudConfig.CloudTypes.LOD;
    @ConfigEntry.Category(value ="cloud_settings")
    public float verticalRenderDistance = 1000;
    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.BoundedDiscrete(min = 2, max = 128)
    public int horizontalRenderDistance = 20;
    @ConfigEntry.Category(value ="cloud_settings")
    public float lodRenderDistance = 50.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.Gui.PrefixText
    public float cloudThickness =  4.0F;
    @ConfigEntry.Category(value ="cloud_settings")
    @ConfigEntry.ColorPicker
    public int cloudColor = 0xffffff;
    @ConfigEntry.Category(value ="cloud_settings")
    public float cloudOpacity = 0.8F;
    @ConfigEntry.Category(value ="cloud_settings")
    public boolean shading = true;
    @ConfigEntry.Category(value ="cloud_settings")
    public float cloudSpeed = 1.0F;
}
