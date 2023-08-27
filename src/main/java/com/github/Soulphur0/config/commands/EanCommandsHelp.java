package com.github.Soulphur0.config.commands;

import net.minecraft.text.Text;

public class EanCommandsHelp {

    public static Text useEanCloudRendering(){
        return Text.of( """
                        
                        --- Use Elytra Aeronautics' cloud rendering ---
                        Set to true to use the multi-layer cloud rendering and cloud customization.
                        Set to false to disable this feature.
                        
                        Default value: true
                        """);
    }

    public static Text setCloudLayerAmount(){
        return Text.of("""
                        
                        --- Cloud layer amount ---
                        Amount of cloud layers to render and make available for customization.
                        Use the 'configCloudLayer' option to select a specific layer or all layers.
                        
                        Default value: 3
                        """);
    }

    public static Text loadPreset(){
        return Text.of("""
                        
                        --- Load cloud preset ---
                        Elytra Aeronautics comes with various cloud presets in order to showcase some of the awesome stuff that can be done with cloud layer customization.
                        Use this option to select a cloud preset.
                        
                        Default value: DEFAULT
                        """);
    }

    public static Text altitude(){
        return Text.of("""
                        
                        --- Layer altitude ---
                        Altitude (Y) at which the selected cloud layer/s will render.
                        The altitude of the two additional cloud layers that come by default mark where flight speed starts to increase and where it reaches its maximum by default configuration, although these parameters are also modifiable in the FlightConfig section. 
                        
                        Default values:
                        First layer: 192.0 (Vanilla value)
                        Second layer: 250.0
                        Third layer: 1000.0
                        """);
    }

    public static Text cloudType(){
        return Text.of("""
                        
                        --- Cloud type ---
                        The type of cloud to render for the selected cloud layer/s. 
                        There are three types:
                        
                        FAST - Vanilla fast clouds
                        FANCY - Vanilla fancy clouds
                        LOD (Level Of Detail) - Clouds that are rendered as 'FAST' when far away and as 'FANCY' when close.
                        
                        The LOD clouds' FAST to FANCY transition distance can be configured.
                        
                        Default values:
                        First layer: FANCY
                        Second layer: LOD
                        Third layer: LOD
                        """);
    }

    public static Text verticalRenderDistance(){
        return Text.of("""
                        
                        --- Cloud layer vertical render distance ---
                        Maximum vertical distance at which the selected cloud layer/s will be rendered.
                        
                        Default value: 1000 (blocks)
                        """);
    }

    public static Text horizontalRenderDistance(){
        return Text.of("""
                        
                        --- Cloud layer horizontal render distance ---
                        Area of the sky that the selected cloud layer/s will occupy.
                        
                        Default value: 15 (chunks) (Vanilla value)
                        """);
    }

    public static Text lodRenderDistance(){
        return Text.of("""
                        
                        --- Cloud layer LOD render distance ---
                        Vertical distance from selected cloud layer/s at which LOD clouds will transition its rendering mode.
                        
                        Being a distance greater than this value from the layer will show the clouds as FAST clouds.
                        
                        Being a distance smaller than this value from the layer will show the clouds as FANCY clouds.
                        
                        The cloud type for the layer must be configured as LOD for this to work. 
                        
                        Default value: 150 (blocks)
                        """);
    }

    public static Text thickness(){
        return Text.of("""
                        
                        --- Cloud thickness ---
                        Amount of blocks that the clouds of the selected layer/s will occupy vertically.
                        
                        Default value: 4 (blocks) (Vanilla value)
                        """);
    }

    public static Text color(){
        return Text.of("""
                        
                        --- Cloud color ---
                        Color that the clouds of the selected cloud layer/s will be rendered with.
                        This value must be introduced as an hexadecimal color code.
                        
                        Default value: FFFFFF (white) (Vanilla value)
                        """);
    }

    public static Text opacity(){
        return Text.of("""
                        
                        --- Cloud opacity ---
                        Opacity that the clouds of the selected cloud layer/s will be rendered with.
                        This value ranges between 1.0 (fully opaque) and 0.0 (fully invisible).
                        
                        Default value: 0.8 (Vanilla value)
                        """);
    }

    public static Text shading(){
        return Text.of("""
                        
                        --- Cloud shading ---
                        Set to true to enable cloud shading, the different sides of the clouds will have different tones.
                        Set to false to disable this feature and render clouds with a solid, monochromatic, color.
                        
                        Default value: true (Vanilla value)
                        """);
    }

    public static Text speed(){
        return Text.of("""
                        
                        --- Cloud speed ---
                        Speed at which the clouds will travel.
                        The input value represents a multiplier for the vanilla cloud speed.
                        This means '2.0' will mean x2 the vanilla cloud speed, '100.0' will mean x100 and so on.
                        
                        Default value: 1.0 (x1.0 speed) (Vanilla value)
                        """);
    }

    public static Text skyEffects(){
        return Text.of("""
                        
                        --- Cloud layer sky effects ---
                        Set to true to let the clouds of the selected layer/s turn darker at night and under weather conditions.
                        Set to false to disable this feature and always render clouds bright, as if they had an emissive texture.
                        When set to false, clouds appear as if they were glowing when the sky is dark.
                        
                        Default value: true (Vanilla value)
                        """);
    }
}
