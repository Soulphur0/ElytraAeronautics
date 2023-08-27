package com.github.Soulphur0.config.commands;

import com.github.Soulphur0.config.EanClientSettings;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
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
                        .append(Text.literal("You can read an in-depth guide about this command ").append(Text.literal("clicking here.\n").formatted(Formatting.UNDERLINE).styled((style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Soulphur0/Soulphur-Mods-ResoucesAndChangelogs/blob/main/ElytraAeronautics/mod_resources/ean_command_guide.md"))))));

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
                                try {
                                    for(int i = 1; i <= CloudConfig.cloudLayers.length; i++){
                                        suggestions.add(String.valueOf(i));
                                    }
                                } catch (NullPointerException e){
                                    suggestions.add("all");
                                }
                            } else if (configMode.equals("CloudConfig") && arg1.equals("loadPreset")){
                                suggestions.add("-help");
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
                            ServerPlayerEntity executor = context.getSource().getPlayer();
                            String configMode = StringArgumentType.getString(context, "configMode");
                            String arg1 = StringArgumentType.getString(context, "arg1");
                            String value = StringArgumentType.getString(context, "arg2");
                            String message = "";

                            if (configMode.equals("FlightConfig") && (context.getSource().hasPermissionLevel(4) || context.getSource().getServer().isSingleplayer())){
                                message = switch (arg1) {
                                    case "altitudeDeterminesSpeed" -> setAltitudeDeterminesSpeed(value);
                                    case "minSpeed" -> setMinSpeed(value);
                                    case "maxSpeed" -> setMaxSpeed(value);
                                    case "minHeight" -> setMinHeight(value);
                                    case "maxHeight" -> setMaxHeight(value);
                                    case "sneakingRealignsPitch" -> setSneakingRealignsPitch(value);
                                    case "realignAngle" -> setRealignAngle(value);
                                    case "realignRate" -> setRealignRate(value);
                                    default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                                };
                            } else if (configMode.equals("FlightConfig") && !context.getSource().hasPermissionLevel(4)){
                                context.getSource().sendMessage(Text.literal("You require to be an operator in order to change elytra flight settings.").formatted(Formatting.RED));
                            } else if (configMode.equals("CloudConfig")) {
                                switch (arg1) {
                                    case "useEanCloudRendering" -> setUseEanCloudRendering(executor, value);
                                    case "setCloudLayerAmount" -> setCloudLayerAmount(executor, value);
                                    case "loadPreset" -> loadPreset(executor, value);
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
                                            suggestions.add("15");
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
                                            suggestions.add("0.8");
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
                                        ServerPlayerEntity executor = context.getSource().getPlayer();
                                        String configMode = StringArgumentType.getString(context, "configMode");

                                        if (configMode.equals("CloudConfig")){
                                            String layerNumber = StringArgumentType.getString(context, "arg2");
                                            String layerAttribute = StringArgumentType.getString(context, "arg3");
                                            String value = StringArgumentType.getString(context, "value");

                                            switch (layerAttribute) {
                                                case "altitude" -> setLayerAltitude(executor, layerNumber, value);
                                                case "cloudType" -> setLayerCloudType(executor, layerNumber, value);
                                                case "verticalRenderDistance" -> setLayerVerticalRenderDistance(executor, layerNumber, value);
                                                case "horizontalRenderDistance" -> setLayerHorizontalRenderDistance(executor, layerNumber, value);
                                                case "lodRenderDistance" -> setLodRenderDistance(executor, layerNumber, value);
                                                case "thickness" -> setLayerCloudThickness(executor, layerNumber, value);
                                                case "color" -> setCloudColor(executor, layerNumber, value);
                                                case "opacity" -> setCloudOpacity(executor, layerNumber, value);
                                                case "shading" -> setShading(executor, layerNumber, value);
                                                case "speed" -> setCloudSpeed(executor, layerNumber, value);
                                                case "skyEffects" -> setSkyEffects(executor, layerNumber, value);
                                                default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.attribute")).create();
                                            }
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setAltitudeDeterminesSpeed(altitudeDeterminesSpeed);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setMinSpeed(minSpeed);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setMaxSpeed(maxSpeed);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setMinHeight(minHeight);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setMaxHeight(maxHeight);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setSneakingRealignsPitch(sneakingRealignsPitch);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setRealignAngle(realignAngle);

            // Write new value to disk, notifying settings change to the server.
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

            // Write new value to current config instance.
            FlightConfig.getOrCreateInstance().setRealignRate(realignRate);

            // Write new value to disk, notifying settings change to the server.
            FlightConfig.writeToDisk();

            return "The realign rate is now set to " + value + " degrees-per-tick.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    // : CLIENT CONFIG ---------------------------------------------------------------------------------------------------

    // $ Cloud general configuration
    private static void setUseEanCloudRendering(ServerPlayerEntity executor, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor, new EanClientSettings("generalCloudConfig","useEanCloudRendering", value));
    }

    private static void setCloudLayerAmount(ServerPlayerEntity executor, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor, new EanClientSettings("generalCloudConfig","setCloudLayerAmount", value));
    }

    private static void loadPreset(ServerPlayerEntity executor, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor, new EanClientSettings("generalCloudConfig","loadPreset", value));
    }

    // $ Cloud layer configuration
    private static void setLayerAltitude(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","altitude", layerNumberArg, value));
    }

    private static void setLayerCloudType(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","cloudType", layerNumberArg, value));
    }

    private static void setLayerVerticalRenderDistance(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","verticalRenderDistance", layerNumberArg, value));
    }

    private static void setLayerHorizontalRenderDistance(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","horizontalRenderDistance", layerNumberArg, value));
    }

    private static void setLodRenderDistance(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","lodRenderDistance", layerNumberArg, value));
    }

    private static void setLayerCloudThickness(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","thickness", layerNumberArg, value));
    }

    private static void setCloudColor(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","color", layerNumberArg, value));
    }

    private static void setCloudOpacity(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","opacity", layerNumberArg, value));
    }

    private static void setShading(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","shading", layerNumberArg, value));
    }

    private static void setCloudSpeed(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","speed", layerNumberArg, value));
    }

    private static void setSkyEffects(ServerPlayerEntity executor, String layerNumberArg, String value) {
        // Send config change to the command executor's client.
        EanServerPacketSender.sendClientConfig(executor,new EanClientSettings("cloudLayerConfig","skyEffects", layerNumberArg, value));
    }
}