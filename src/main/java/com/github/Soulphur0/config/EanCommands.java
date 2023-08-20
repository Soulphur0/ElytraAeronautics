package com.github.Soulphur0.config;

import com.github.Soulphur0.config.objects.CloudLayer;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class EanCommands {

    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("ean")
            // < HELP INFO
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayer();
                Text message = Text.literal("\n")
                        .append(Text.literal("Type '/ean' and press TAB to see available options.\nWrite '-help' at the end of each option to see its usage and default values.\n\n").formatted(Formatting.GOLD))
                        .append(Text.literal("You can read an in-depth guide about this command ").append(Text.literal("clicking here.\n").formatted(Formatting.UNDERLINE).styled((style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Soulphur0/ElytraAeronautics"))))));

                if (player != null)
                    player.sendMessage(message);

                return 1;
            })
            // $ CHOOSE CONFIG CATEGORY
            // € Either FlightConfig or CloudConfig
            .then(argument("configMode", string())
                .suggests((commandContext, suggestionsBuilder) -> {
                    String[] suggestions = {"FlightConfig", "CloudConfig"};

                    return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                })
                // + CHOOSE OPTION
                // * FlightOptions (enable/disable, speed, height, ...)
                // * CloudOptions (enable/disable, layerAmount, ...) OR (layerConfig, loadPreset) subcommands.
                .then(argument("arg1", string())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        String configMode = StringArgumentType.getString(commandContext, "configMode");
                        Collection<String> suggestions = new ArrayList<>();

                        // ? Suggestions for each FlightConfig option.
                        if (configMode.equals("FlightConfig")){
                            for(FlightConfig.Options option : FlightConfig.Options.values()){
                                suggestions.add(option.toString());
                            }
                        // ? Suggestions for each CloudConfig option.
                        } else if (configMode.equals("CloudConfig")) {
                            for(CloudConfig.Options option : CloudConfig.Options.values()){
                                suggestions.add(option.toString());
                            }
                        }
                        return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                    })
                    // + Choose a value for the setting
                    // * FlightOptionValue (speed, height, realign angle, ...)
                    // * CloudOptionValue (enableRendering, layerAmount, ...) OR (layerNumber/cloudPreset selection)
                    .then(argument("arg2", string())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String configMode = StringArgumentType.getString(commandContext, "configMode");
                            String arg1 = StringArgumentType.getString(commandContext, "arg1");
                            Collection<String> suggestions = new ArrayList<>();

                            // ? Suggestions for CloudConfig
                            if (configMode.equals("CloudConfig") && arg1.equals("configCloudLayer")) {
                                suggestions.add("all");
                                for(int i = 1; i <= CloudConfig.cloudLayers.length; i++){
                                    suggestions.add(String.valueOf(i));
                                }
                            } else if (configMode.equals("CloudConfig") && arg1.equals("loadPreset")){
                                for(CloudConfig.Presets preset : CloudConfig.Presets.values()){
                                    suggestions.add(preset.toString());
                                }
                            } else if (configMode.equals("CloudConfig") && arg1.equals("useEanCloudRendering")){
                                suggestions.add("-help");
                                suggestions.add("true");
                                suggestions.add("false");
                            } else if (configMode.equals("CloudConfig") && arg1.equals("setCloudLayerAmount")){
                                suggestions.add("-help");
                                suggestions.add("3");
                            }

                            // ? Suggestions for FlightConfig
                            if (configMode.equals("FlightConfig") && arg1.equals("altitudeDeterminesSpeed")) {
                                suggestions.add("-help");
                                suggestions.add("true");
                                suggestions.add("false");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("minSpeed")){
                                suggestions.add("-help");
                                suggestions.add("30.35");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("maxSpeed")){
                                suggestions.add("-help");
                                suggestions.add("257.22");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("minHeight")){
                                suggestions.add("-help");
                                suggestions.add("250.0");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("maxHeight")){
                                suggestions.add("-help");
                                suggestions.add("1000.0");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("sneakingRealignsPitch")){
                                suggestions.add("-help");
                                suggestions.add("true");
                                suggestions.add("false");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("realignAngle")){
                                suggestions.add("-help");
                                suggestions.add("0.0");
                            } else if (configMode.equals("FlightConfig") && arg1.equals("realignRate")){
                                suggestions.add("-help");
                                suggestions.add("0.1");
                            }

                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        })
                        // < FlightConfigScreen & GeneralCloudConfig config execution.
                        .executes(context -> {
                            String configMode = StringArgumentType.getString(context, "configMode");
                            String arg1 = StringArgumentType.getString(context, "arg1");
                            String value = StringArgumentType.getString(context, "arg2");
                            String message = "";

                            if (configMode.equals("FlightConfig")){
                                switch (arg1) {
                                    case "altitudeDeterminesSpeed" -> message = setAltitudeDeterminesSpeed(value);
                                    case "minSpeed" -> message = setMinSpeed(value);
                                    case "maxSpeed" -> message = setMaxSpeed(value);
                                    case "minHeight" -> message = setMinHeight(value);
                                    case "maxHeight" -> message = setMaxHeight(value);
                                    case "sneakingRealignsPitch" -> message = setSneakingRealignsPitch(value);
                                    case "realignAngle" -> message = setRealignAngle(value);
                                    case "realignRate" -> message = setRealignRate(value);
                                    default -> {
                                    }
                                }
                            } else if (configMode.equals("CloudConfig")) {
                                message = switch (arg1) {
                                    case "useEanCloudRendering" -> setUseEanCloudRendering(value);
                                    case "setCloudLayerAmount" -> setCloudLayerAmount(value);
                                    case "loadPreset" -> loadPreset(value);
                                    default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                                };
                            }

                            if (!message.equals(""))
                                context.getSource().sendMessage(Text.of(message));
                            return 1;
                        })
                        // + Cloud layer subcommand option
                        // * Altitude, speed, color, thickness, ...
                        .then(argument("arg3", string())
                            // ? Suggestions of each cloud option.
                            .suggests((commandContext, suggestionsBuilder) -> {
                                String configMode = StringArgumentType.getString(commandContext, "configMode");
                                Collection<String> suggestions = new ArrayList<>();
                                if (configMode.equals("CloudConfig")){
                                    for(CloudConfig.LayerAttributes attribute : CloudConfig.LayerAttributes.values()){
                                        suggestions.add(attribute.toString());
                                    }
                                }
                                return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                            })

                            // + Cloud layer option value.
                            .then(argument("value", string())
                                .suggests((commandContext, suggestionsBuilder) -> {
                                    String configMode = StringArgumentType.getString(commandContext, "configMode");
                                    Collection<String> suggestions = new ArrayList<>();

                                    // ? Suggestions for each option, numerical values show their default setting.
                                    if (configMode.equals("CloudConfig")){
                                        String layerAttribute = StringArgumentType.getString(commandContext, "arg3");

                                        if (Objects.equals(layerAttribute, "altitude")){
                                            suggestions.add("-help");
                                            suggestions.add("192");
                                        } else if (Objects.equals(layerAttribute, "cloudType")){
                                            suggestions.add("-help");
                                            suggestions.add("LOD");
                                            suggestions.add("Fancy");
                                            suggestions.add("Fast");
                                        } else if (Objects.equals(layerAttribute, "verticalRenderDistance")){
                                            suggestions.add("-help");
                                            suggestions.add("1000");
                                        } else if (Objects.equals(layerAttribute, "horizontalRenderDistance")){
                                            suggestions.add("-help");
                                            suggestions.add("20");
                                        } else if (Objects.equals(layerAttribute, "lodRenderDistance")){
                                            suggestions.add("-help");
                                            suggestions.add("150");
                                        } else if (Objects.equals(layerAttribute, "thickness")){
                                            suggestions.add("-help");
                                            suggestions.add("4.0");
                                        } else if (Objects.equals(layerAttribute, "color")){
                                            suggestions.add("-help");
                                            suggestions.add("FFFFFF");
                                        } else if (Objects.equals(layerAttribute, "opacity")){
                                            suggestions.add("-help");
                                            suggestions.add("1.0");
                                        } else if (Objects.equals(layerAttribute, "shading")){
                                            suggestions.add("-help");
                                            suggestions.add("true");
                                            suggestions.add("false");
                                        } else if (Objects.equals(layerAttribute, "speed")){
                                            suggestions.add("-help");
                                            suggestions.add("1.0");
                                        } else if (Objects.equals(layerAttribute, "skyEffects")){
                                            suggestions.add("-help");
                                            suggestions.add("true");
                                            suggestions.add("false");
                                        }
                                    }
                                    return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                                })
                                    // < CloudLayer config execution.
                                    .executes(context -> {
                                        String configMode = StringArgumentType.getString(context, "configMode");

                                        if (configMode.equals("CloudConfig")){
                                            String layerNumber = StringArgumentType.getString(context, "arg2");
                                            String layerAttribute = StringArgumentType.getString(context, "arg3");
                                            String value = StringArgumentType.getString(context, "value");
                                            String message = "";

                                            switch (layerAttribute) {
                                                case "altitude" -> message = setLayerAltitude(layerNumber, value);
                                                case "cloudType" -> message = setLayerCloudType(layerNumber, value);
                                                case "verticalRenderDistance" -> message = setLayerVerticalRenderDistance(layerNumber, value);
                                                case "horizontalRenderDistance" -> message = setLayerHorizontalRenderDistance(layerNumber, value);
                                                case "lodRenderDistance" -> message = setLodRenderDistance(layerNumber, value);
                                                case "thickness" -> message = setLayerCloudThickness(layerNumber, value);
                                                case "color" -> message = setCloudColor(layerNumber, value);
                                                case "opacity" -> message = setCloudOpacity(layerNumber, value);
                                                case "shading" -> message = setShading(layerNumber, value);
                                                case "speed" -> message = setCloudSpeed(layerNumber, value);
                                                case "skyEffects" -> message = setSkyEffects(layerNumber, value);
                                                default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.attribute")).create();
                                            }
                                            if (!message.equals(""))
                                                context.getSource().sendMessage(Text.of(message));
                                        }
                                        return 1;
                                    })
                            )
                        ))
                )
            )
        ));
    }

    // $ Elytra flight configuration
    private static String setAltitudeDeterminesSpeed(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Altitude determines flight speed ---
                        Set to true to make elytra flight faster at higher altitudes. 
                        Set to false to disable this feature.
                        Flight speed will be determined by the minSpeed setting.
                        
                        Default value: true
                        """;
        try{
            boolean altitudeDeterminesSpeed = Boolean.parseBoolean(value);
            FlightConfig.getOrCreateInstance().setAltitudeDeterminesSpeed(altitudeDeterminesSpeed);

            FlightConfig.writeToDisk();
            return (altitudeDeterminesSpeed) ? "Altitude now determines elytra flight speed." : "Altitude no longer determines elytra flight speed.";
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMinSpeed(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Minimal flight speed ---
                        Minimal flight speed achieved by travelling at a zero degree angle.
                        
                        Default value: 30.35 (m/s) (Vanilla flight speed)
                        """;
        try{
            double minSpeed = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMinSpeed(minSpeed);

            FlightConfig.writeToDisk();
            return "Minimum flight speed is now " + value + "m/s";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMaxSpeed(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Maximum flight speed ---
                        Maximum flight speed achieved by travelling at a zero degree angle.
                        
                        Default value: 257.22 (m/s)
                        """;
        try{
            double maxSpeed = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMaxSpeed(maxSpeed);

            FlightConfig.writeToDisk();
            return "Maximum flight speed is now " + value + "m/s";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMinHeight(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Minimal height ---
                        Altitude (Y) at which flight speed starts to increase.
                        
                        Default value: 250
                        """;
        try{
            double minHeight = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMinHeight(minHeight);

            FlightConfig.writeToDisk();
            return "The minimum height at which flight speed increases is now " + value + "m of altitude.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMaxHeight(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Maximum height ---
                        Altitude (Y) at which flight speed reaches its maximum.
                        
                        Default value: 1000
                        """;
        try{
            double maxHeight = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMaxHeight(maxHeight);

            FlightConfig.writeToDisk();
            return "The maximum height at which flight speed increases is now " + value + "m of altitude.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setSneakingRealignsPitch(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Sneaking realigns pitch ---
                        Set to true to realign your pitch when sneaking mid-flight.
                        Set to false to disable this feature.
                        
                        Default value: true
                        """;
        try{
            boolean sneakingRealignsPitch = Boolean.parseBoolean(value);
            FlightConfig.getOrCreateInstance().setSneakingRealignsPitch(sneakingRealignsPitch);

            FlightConfig.writeToDisk();
            return (sneakingRealignsPitch) ? "Sneaking mid flight now realigns flight pitch." : "Sneaking mid flight no longer realigns flight pitch.";
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setRealignAngle(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Pitch realignment angle ---
                        Pitch angle at which the player will align towards when sneaking mid-flight.
                        
                        Default value: 0.0 (degrees)
                        """;
        try{
            float realignAngle = Float.parseFloat(value);
            FlightConfig.getOrCreateInstance().setRealignAngle(realignAngle);

            FlightConfig.writeToDisk();
            return "The realign angle is now set to " + value + " degrees.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setRealignRate(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Pitch realignment rate ---
                        Amount of rotation at which the player will realign towards the "realignAngle".
                        
                        Default value: 0.1 (degrees-per-tick)
                        """;
        try{
            float realignRate = Float.parseFloat(value);
            FlightConfig.getOrCreateInstance().setRealignRate(realignRate);

            FlightConfig.writeToDisk();
            return "The realign rate is now set to " + value + " degrees-per-tick.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    // $ Cloud general configuration
    private static String setUseEanCloudRendering(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Use Elytra Aeronautics' cloud rendering ---
                        Set to true to use the multi-layer cloud rendering and cloud customization.
                        Set to false to disable this feature.
                        
                        Default value: true
                        """;
        try{
            boolean useEanCloudRendering = Boolean.parseBoolean(value);
            CloudConfig.getOrCreateInstance().setUseEanClouds(useEanCloudRendering);

            CloudConfig.writeToDisk();
            return (useEanCloudRendering) ? "Elytra Aeronautics cloud rendering is now enabled." : "Elytra Aeronautics cloud rendering is now disabled.";
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setCloudLayerAmount(String value) throws CommandSyntaxException{
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud layer amount ---
                        Amount of cloud layers to render and make available for customization.
                        Use the 'configCloudLayer' option to select a specific layer or all layers.
                        
                        Default value: 3
                        """;
        try {
            int amount = Integer.parseInt(value);
            CloudConfig.getOrCreateInstance().setNumberOfLayers(amount);

            CloudConfig.writeToDisk();
            return "Set number of cloud layers to " + amount;
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String loadPreset(String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Load cloud preset ---
                        Elytra Aeronautics comes with various cloud presets in order to showcase some of the awesome stuff that can be done with cloud layer customization.
                        Use this option to select a cloud preset.
                        
                        Default value: DEFAULT
                        """;
        try{
            String preset = value.toUpperCase();

            switch (preset){
                case "DEFAULT" -> CloudConfig.cloudPreset_default();
                case "DENSE_AND_PUFFY" -> CloudConfig.cloudPreset_denseAndPuffy();
                case "WINDY" -> CloudConfig.cloudPreset_windy();
                case "RAINBOW" -> CloudConfig.cloudPreset_rainbow();
                case "SKY_HIGHWAY" -> CloudConfig.cloudPreset_skyHighway();
                case "SEA_MIST" -> CloudConfig.cloudPreset_seaMist();
            }

            CloudConfig.writeToDisk();
            return "Loaded the " + value + " cloud preset.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    // $ Cloud layer configuration
    private static String setLayerAltitude(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Layer altitude ---
                        Altitude (Y) at which the selected cloud layer/s will render.
                        The altitude of the two additional cloud layers that come by default mark where flight speed starts to increase and where it reaches its maximum by default configuration, although these parameters are also modifiable in the FlightConfig section. 
                        
                        Default values:
                        First layer: 192.0 (Vanilla value)
                        Second layer: 250.0
                        Third layer: 1000.0
                        """;
        try{
            double altitude = Double.parseDouble(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setAltitude(altitude);
                }
                CloudConfig.writeToDisk();
                return "Set altitude of all layers to " + altitude;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setAltitude(altitude);

            CloudConfig.writeToDisk();
            return "Set altitude of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getAltitude();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudType(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
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
                        """;
        try{
            CloudConfig.CloudTypes cloudType = CloudConfig.CloudTypes.valueOf(value.toUpperCase());

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudType(cloudType);
                }
                CloudConfig.writeToDisk();
                return "Set cloud type of all layers to " + cloudType;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudType(cloudType);

            CloudConfig.writeToDisk();
            return "Set cloud type of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudType();
        } catch (IllegalArgumentException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerVerticalRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud layer vertical render distance ---
                        Maximum vertical distance at which the selected cloud layer/s will be rendered.
                        
                        Default value: 1000 (blocks)
                        """;
        try{
            float verticalRenderDistance = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setVerticalRenderDistance(verticalRenderDistance);
                }
                CloudConfig.writeToDisk();
                return "Set vertical render distance of all layers to " + verticalRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setVerticalRenderDistance(verticalRenderDistance);

            CloudConfig.writeToDisk();
            return "Set vertical render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getVerticalRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerHorizontalRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud layer horizontal render distance ---
                        Area of the sky that the selected cloud layer/s will occupy.
                        
                        Default value: 20 (chunks) (Vanilla value)
                        """;
        try{
            int horizontalRenderDistance = Integer.parseInt(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setHorizontalRenderDistance(horizontalRenderDistance);
                }
                CloudConfig.writeToDisk();
                return "Set horizontal render distance of all layers to " + horizontalRenderDistance + " chunks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setHorizontalRenderDistance(horizontalRenderDistance);

            CloudConfig.writeToDisk();
            return "Set horizontal render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getHorizontalRenderDistance() + " chunks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLodRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud layer LOD render distance ---
                        Vertical distance from selected cloud layer/s at which LOD clouds will transition its rendering mode.
                        
                        Being a distance greater than this value from the layer will show the clouds as FAST clouds.
                        
                        Being a distance smaller than this value from the layer will show the clouds as FANCY clouds.
                        
                        The cloud type for the layer must be configured as LOD for this to work. 
                        
                        Default value: 150 (blocks)
                        """;
        try{
            float lodRenderDistance = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setLodRenderDistance(lodRenderDistance);
                }
                CloudConfig.writeToDisk();
                return "Set LOD render distance of all layers to " + lodRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setLodRenderDistance(lodRenderDistance);

            CloudConfig.writeToDisk();
            return "Set LOD render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getLodRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudThickness(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud thickness ---
                        Amount of blocks that the clouds of the selected layer/s will occupy vertically.
                        
                        Default value: 4 (blocks) (Vanilla value)
                        """;
        try{
            float cloudThickness = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudThickness(cloudThickness);
                }
                CloudConfig.writeToDisk();
                return "Set cloud thickness of all layers to " + cloudThickness + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudThickness(cloudThickness);

            CloudConfig.writeToDisk();
            return "Set cloud thickness of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudThickness() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudColor(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud color ---
                        Color that the clouds of the selected cloud layer/s will be rendered with.
                        This value must be introduced as an hexadecimal color code.
                        
                        Default value: FFFFFF (white) (Vanilla value)
                        """;
        try{
            int cloudColor = Integer.parseInt(value,16);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudColor(cloudColor);
                }
                CloudConfig.writeToDisk();
                return "Set cloud color of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudColor(cloudColor);

            CloudConfig.writeToDisk();
            return "Set cloud color of layer " + layerNumber + " to " + value + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudOpacity(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud opacity ---
                        Opacity that the clouds of the selected cloud layer/s will be rendered with.
                        This value ranges between 1.0 (fully opaque) and 0.0 (fully invisible).
                        
                        Default value: 0.8 (Vanilla value)
                        """;
        try{
            float cloudOpacity = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudOpacity(cloudOpacity);
                }
                CloudConfig.writeToDisk();
                return "Set cloud opacity of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudOpacity(cloudOpacity);

            CloudConfig.writeToDisk();
            return "Set cloud opacity of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudOpacity() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setShading(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud shading ---
                        Set to true to enable cloud shading, the different sides of the clouds will have different tones.
                        Set to false to disable this feature and render clouds with a solid, monochromatic, color.
                        
                        Default value: true (Vanilla value)
                        """;
        try{
            boolean shading = Boolean.parseBoolean(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setShading(shading);
                }
                CloudConfig.writeToDisk();
                return "Set shading of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setShading(shading);

            CloudConfig.writeToDisk();
            return "Set shading of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].isShading() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudSpeed(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud speed ---
                        Speed at which the clouds will travel.
                        The input value represents a multiplier for the vanilla cloud speed.
                        This means '2.0' will mean x2 the vanilla cloud speed, '100.0' will mean x100 and so on.
                        
                        Default value: 1.0 (x1.0 speed) (Vanilla value)
                        """;
        try{
            float cloudSpeed = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudSpeed(cloudSpeed);
                }
                CloudConfig.writeToDisk();
                return "Set speed of all layers to x" + value + " speed.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudSpeed(cloudSpeed);

            CloudConfig.writeToDisk();
            return "Set speed of layer " + layerNumber + " to x" + CloudConfig.cloudLayers[layerNumber-1].getCloudSpeed() + " speed.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setSkyEffects(String layerNumberArg, String value) throws CommandSyntaxException {
        if (Objects.equals(value, "-help"))
            return """
                        
                        --- Cloud layer sky effects ---
                        Set to true to let the clouds of the selected layer/s turn darker at night and under weather conditions.
                        Set to false to disable this feature and always render clouds bright, as if they had an emissive texture.
                        When set to false, clouds appear as if they were glowing when the sky is dark.
                        
                        Default value: true (Vanilla value)
                        """;
        try{
            boolean skyEffects = Boolean.parseBoolean(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setSkyEffects(skyEffects);
                }
                CloudConfig.writeToDisk();
                return "Set sky effects of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setSkyEffects(skyEffects);

            CloudConfig.writeToDisk();
            return "Set sky effects of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].isSkyEffects() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }
}