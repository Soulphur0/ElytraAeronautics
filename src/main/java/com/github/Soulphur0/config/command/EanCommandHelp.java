package com.github.Soulphur0.config.command;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EanCommandHelp {

    // : FLIGHT CONFIG ---------------------------------------------------------------------------------------------------
    public static Text setAltitudeDeterminesSpeed(){
        return Text.literal("\n")
                .append(Text.literal("Altitude determines flight speed").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Makes elytra flight faster at higher altitudes.
                      
                        """
                ))
                .append(Text.literal("Default value: true").formatted(Formatting.AQUA));
    }

    public static Text setMinSpeed(){
        return Text.literal("\n")
                .append(Text.literal("Minimal flight speed").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Minimal flight speed achieved by travelling at a pitch of 0°.
                        This speed may increase a little when 'nosediving'.
                        
                        It can be lowered to achieve speeds lower than vanilla's.
                      
                        """
                ))
                .append(Text.literal("Default value: 30.35 (m/s) (Vanilla flight speed)").formatted(Formatting.AQUA));
    }

    public static Text setMaxSpeed(){
        return Text.literal("\n")
                .append(Text.literal("Maximum flight speed").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Maximum flight speed achieved by travelling at a pitch of 0°.
                        This speed may increase a little when 'nosediving'.
                        
                        This value can be cranked up to the hundred of thousands of
                        blocks per second but it is highly recommended to adjust the
                        WorldRendering options accordingly.
                      
                        """
                ))
                .append(Text.literal("Default value: 257.22 (m/s)").formatted(Formatting.AQUA));
    }

    public static Text setMinHeight(){
        return Text.literal("\n")
                .append(Text.literal("Minimal height").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Altitude at which flight speed starts to increase.
                      
                        """
                ))
                .append(Text.literal("Default value: 250 (Y)").formatted(Formatting.AQUA));
    }

    public static Text setMaxHeight(){
        return Text.literal("\n")
                .append(Text.literal("Maximum height").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Altitude at which flight speed reaches its maximum.
                      
                        """
                ))
                .append(Text.literal("Default value: 1000 (Y)").formatted(Formatting.AQUA));
    }

    public static Text setSneakingRealignsPitch(){
        return Text.literal("\n")
                .append(Text.literal("Sneaking realigns pitch").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Makes the player realign their pitch when sneaking mid-flight.
                      
                        """
                ))
                .append(Text.literal("Default value: true").formatted(Formatting.AQUA));
    }

    public static Text setRealignAngle(){
        return Text.literal("\n")
                .append(Text.literal("Pitch realignment angle").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Pitch angle at which the player aligns
                        towards when sneaking mid-flight.
                        
                        Measured in angle degrees.
                      
                        """
                ))
                .append(Text.literal("Default value: 0.0 (degrees)").formatted(Formatting.AQUA));
    }

    public static Text setRealignRate(){
        return Text.literal("\n")
                .append(Text.literal("Pitch realignment rate").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Amount of rotation at which players realign
                        towards the "realignAngle".
                        
                        Measured in degrees-per-tick.
                      
                        """
                ))
                .append(Text.literal("Default value: 0.1 (degrees-per-tick)").formatted(Formatting.AQUA));
    }

    // : WORLD RENDERING CONFIG ---------------------------------------------------------------------------------------------------
    public static Text setUseEanChunkUnloading(){
        return Text.literal("\n")
                .append(Text.literal("Use chunk unloading").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Stop chunk load or generation given certain set conditions.
                        Those can be set with the 'setChunkUnloadingCondition' option.
                      
                        """
                ))
                .append(Text.literal("Default value: true").formatted(Formatting.AQUA));
    }

    public static Text setChunkUnloadingCondition(){
        return Text.literal("\n")
                .append(Text.literal("Chunk unloading condition").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal("\n[speed/height/speed_or_height/speed_and_height]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Determines when chunks will stop loading and generating.
                        This will happen when exceeding a flight speed and/or height.
                        These thresholds can be adjusted using the other options.
                      
                        """
                ))
                .append(Text.literal("Default value: SPEED_OR_HEIGHT").formatted(Formatting.AQUA));
    }

    public static Text setChunkUnloadingSpeed(){
        return Text.literal("\n")
                .append(Text.literal("Chunk unloading speed threshold").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Speed at which chunks will stop to load while elytra-flying.
                        Measured in blocks/meters per second.
                      
                        """
                ))
                .append(Text.literal("Default value: 100 (m/s)").formatted(Formatting.AQUA));
    }

    public static Text setChunkUnloadingHeight(){
        return Text.literal("\n")
                .append(Text.literal("Chunk unloading height threshold").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Altitude at which chunks will stop to load while elytra-flying.
                        This number represents a 'Y' coordinate.
                      
                        """
                ))
                .append(Text.literal("Default value: 320 (Y)").formatted(Formatting.AQUA));
    }


    // : CLOUD CONFIG ---------------------------------------------------------------------------------------------------

    public static Text useEanCloudRendering(){
        return Text.literal("\n")
                .append(Text.literal("Use custom cloud rendering").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Use the multi-layer cloud rendering and cloud customization.
                      
                        """
                ))
                .append(Text.literal("Default value: true").formatted(Formatting.AQUA));
    }

    public static Text setCloudLayerAmount(){
        return Text.literal("\n")
                .append(Text.literal("Cloud layer amount").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Amount of cloud layers to render.
                        Use the 'configCloudLayer' option to config one or all layers.
                      
                        """
                ))
                .append(Text.literal("Default value: 3").formatted(Formatting.AQUA));
    }

    public static Text loadPreset(){
        return Text.literal("\n")
                .append(Text.literal("Load cloud preset").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [select]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Use this option to select a cloud preset:
                        Default, Windy, Puffy, Rainbow, Sky_highway, Sea_mist.
                        
                        Give them a try to see the potential of cloud customization.
                      
                        """
                ))
                .append(Text.literal("Default value: DEFAULT").formatted(Formatting.AQUA));
    }

    public static Text altitude(){
        return Text.literal("\n")
                .append(Text.literal("Layer altitude").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Altitude at which the selected cloud layer/s will render.
                        
                        """
                ))
                .append(Text.literal("""
                        
                        Values of the default layers:
                        
                        192.0 (Vanilla value)
                        250.0 (speed curve start)
                        1000.0 (speed curve end)
                        
                        """).formatted(Formatting.AQUA));
    }

    public static Text cloudType(){
        return Text.literal("\n")
                .append(Text.literal("Cloud type").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [select]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        The type of cloud to render.
                        There are three types:
                        
                        FAST - Vanilla fast clouds
                        FANCY - Vanilla fancy clouds
                        LOD - 'FAST' when far away, 'FANCY' when close to them.
                        
                        The LOD clouds' FAST to FANCY transition distance can be set.

                        """
                ))
                .append(Text.literal("Values of the default layers: FANCY, LOD, LOD").formatted(Formatting.AQUA));
    }

    public static Text verticalRenderDistance(){
        return Text.literal("\n")
                .append(Text.literal("Vertical render distance").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Maximum vertical distance at which the cloud layer renders.
                        Measured in blocks from the cloud layer, vertically.

                        """
                ))
                .append(Text.literal("Default value: 1000 (blocks)").formatted(Formatting.AQUA));
    }

    public static Text horizontalRenderDistance(){
        return Text.literal("\n")
                .append(Text.literal("Horizontal render distance").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Area of the sky that the selected cloud layer will occupy.
                        Measured in approximations of chunks, like vanilla Minecraft.

                        """
                ))
                .append(Text.literal("Default value: 15 (chunks) (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text lodRenderDistance(){
        return Text.literal("\n")
                .append(Text.literal("LOD render distance").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Vertical distance at which LOD clouds will change.
                        Below this value clouds are shown as FANCY.
                        Above this value clouds are shown as FAST.
                        
                        The cloud type for the layer must be LOD for this to work.

                        """
                ))
                .append(Text.literal("Default value: 150 (blocks)").formatted(Formatting.AQUA));
    }

    public static Text thickness(){
        return Text.literal("\n")
                .append(Text.literal("Cloud thickness").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Amount of blocks that the clouds will occupy vertically.

                        """
                ))
                .append(Text.literal("Default value: 4 (blocks) (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text color(){
        return Text.literal("\n")
                .append(Text.literal("Cloud color").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [hexadecimal]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Color that the clouds will be rendered with.
                        This value must be an hexadecimal color code.

                        """
                ))
                .append(Text.literal("Default value: FFFFFF (white) (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text opacity(){
        return Text.literal("\n")
                .append(Text.literal("Cloud opacity").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Opacity that the clouds will be rendered with.
                        It ranges between 1.0 (opaque) and 0.0 (invisible).

                        """
                ))
                .append(Text.literal("Default value: 0.8 (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text shading(){
        return Text.literal("\n")
                .append(Text.literal("Cloud shading").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        The different sides of the clouds will have different tones.
                        Otherwise renders clouds with a solid, monochromatic, color.

                        """
                ))
                .append(Text.literal("Default value: true (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text speed(){
        return Text.literal("\n")
                .append(Text.literal("Cloud speed").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [numerical]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Speed at which the clouds will travel.
                        This value represents a multiplier for the vanilla cloud speed.
                        
                        For example, '2.0' will mean x2 the vanilla cloud speed.

                        """
                ))
                .append(Text.literal("Default value: 1.0 (x1.0 speed) (Vanilla value)").formatted(Formatting.AQUA));
    }

    public static Text skyEffects(){
        return Text.literal("\n")
                .append(Text.literal("Sky effects").formatted(Formatting.BOLD).formatted(Formatting.GOLD))
                .append(Text.literal(" [true/false]\n").formatted(Formatting.BOLD).formatted(Formatting.DARK_AQUA))
                .append(Text.of(
                        """
                        
                        Clouds turn darker at night and under weather conditions.
                        When disabled, clouds render like an emissive texture.

                        """
                ))
                .append(Text.literal("Default value: true (Vanilla value)").formatted(Formatting.AQUA));
    }
}
