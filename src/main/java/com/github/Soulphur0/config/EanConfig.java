package com.github.Soulphur0.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ean")
public class EanConfig implements ConfigData {

    // : Elytra flight fields
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean altitudeDeterminesSpeed = true;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public double minSpeed = 30.35;
    @ConfigEntry.Gui.Tooltip
    public double maxSpeed = 257.22;
    @ConfigEntry.Gui.Tooltip
    public double minHeight = 250.0;
    @ConfigEntry.Gui.Tooltip
    public double maxHeight = 1000.0;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip
    public boolean sneakingRealignsPitch = true;
    @ConfigEntry.Gui.Tooltip
    public float realignAngle = 0.0F;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public float realignRate = 0.1F;

    // : Cloud layers fields

}
