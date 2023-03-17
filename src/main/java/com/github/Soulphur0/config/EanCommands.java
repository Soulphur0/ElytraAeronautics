package com.github.Soulphur0.config;

import com.github.Soulphur0.config.objects.CloudLayer;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class EanCommands {

    public static void register(){
        // $ Cloud layer config command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("ean")
            // + Config mode argument
            .then(argument("configMode", string())
                // - Config option suggestions
                .suggests((commandContext, suggestionsBuilder) -> {
                    String[] suggestions = {"FlightConfigScreen", "Test2"};

                    return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                })
                // + 1# multi-type argument
                // * ElytraFlightOption
                // * CloudLayerOption
                .then(argument("arg1", string())
                    // - Suggestions
                    .suggests((commandContext, suggestionsBuilder) -> {
                        String configMode = StringArgumentType.getString(commandContext, "configMode");
                        Collection<String> suggestions = new ArrayList<>();
                        if (configMode.equals("FlightConfigScreen")){
                            for(FlightConfig.Options option : FlightConfig.Options.values()){
                                suggestions.add(option.toString());
                            }
                        } else if (configMode.equals("Test2")) {
                            suggestions.add("disableEanCloudRendering");
                            suggestions.add("layerAmount");
                            suggestions.add("configCloudLayer");
                        }
                        return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                    })
                    // + 2# multi-type argument
                    // * ElytraFlightOptionValue
                    // * CloudLayerNumber
                    .then(argument("arg2", string())
                        // - Suggestions
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String configMode = StringArgumentType.getString(commandContext, "configMode");
                            String arg1 = StringArgumentType.getString(commandContext, "arg1");
                            Collection<String> suggestions = new ArrayList<>();
                            if (configMode.equals("Test2") && arg1.equals("configCloudLayer")) {
                                suggestions.add("all");
                                for(int i = 1; i <= CloudConfig.cloudLayers.length; i++){
                                    suggestions.add(String.valueOf(i));
                                }
                            }
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        })
                        // = FlightConfigScreen config execution
                        .executes(context -> {
                            String configMode = StringArgumentType.getString(context, "configMode");
                            String arg1 = StringArgumentType.getString(context, "arg1");
                            String value = StringArgumentType.getString(context, "arg2");
                            String message = "";

                            if (configMode.equals("FlightConfigScreen")){
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
                            } else if (configMode.equals("Test2")) {
                                switch (arg1){
                                    case "diableEanCloudRendering":

                                        break;
                                    case "layerAmount":

                                        break;
                                    default:
                                        throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                                }

                                message = "Cloud layer value is " + value;
                            }

                            context.getSource().sendMessage(Text.of(message));
                            return 1;
                        })
                    // + Cloud layer attribute argument
                    .then(argument("arg3", string())
                        // - Suggestions
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String configMode = StringArgumentType.getString(commandContext, "configMode");
                            Collection<String> suggestions = new ArrayList<>();
                            if (configMode.equals("Test2")){
                                for(CloudConfig.LayerAttributes attribute : CloudConfig.LayerAttributes.values()){
                                    suggestions.add(attribute.toString());
                                }
                            }
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        })

                        // + Layer attribute value argument
                        .then(argument("value", string())
                            // = Test2 config execution
                            .executes(context -> {
                                String configMode = StringArgumentType.getString(context, "configMode");

                                if (configMode.equals("Test2")){
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
        try{
            boolean altitudeDeterminesSpeed = Boolean.parseBoolean(value);
            FlightConfig.getOrCreateInstance().setAltitudeDeterminesSpeed(altitudeDeterminesSpeed);

            return (altitudeDeterminesSpeed) ? "Altitude now determines elytra flight speed." : "Altitude no longer determines elytra flight speed";
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMinSpeed(String value) throws CommandSyntaxException {
        try{
            double minSpeed = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMinSpeed(minSpeed);

            return "Minimum flight speed is now " + value + "m/s";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMaxSpeed(String value) throws CommandSyntaxException {
        try{
            double maxSpeed = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMaxSpeed(maxSpeed);

            return "Maximum flight speed is now " + value + "m/s";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMinHeight(String value) throws CommandSyntaxException {
        try{
            double minHeight = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMinHeight(minHeight);

            return "The minimum height at which flight speed increases is now " + value + "m of altitude.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setMaxHeight(String value) throws CommandSyntaxException {
        try{
            double maxHeight = Double.parseDouble(value);
            FlightConfig.getOrCreateInstance().setMaxHeight(maxHeight);

            return "The maximum height at which flight speed increases is now " + value + "m of altitude.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setSneakingRealignsPitch(String value) throws CommandSyntaxException {
        try{
            boolean sneakingRealignsPitch = Boolean.parseBoolean(value);
            FlightConfig.getOrCreateInstance().setSneakingRealignsPitch(sneakingRealignsPitch);

            return (sneakingRealignsPitch) ? "Sneaking mid flight now realigns flight pitch." : "Sneaking mid flight no longer realigns flight pitch.";
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setRealignAngle(String value) throws CommandSyntaxException {
        try{
            float realignAngle = Float.parseFloat(value);
            FlightConfig.getOrCreateInstance().setRealignAngle(realignAngle);

            return "The realign angle is now set to " + value + " degrees.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setRealignRate(String value) throws CommandSyntaxException {
        try{
            float realignRate = Float.parseFloat(value);
            FlightConfig.getOrCreateInstance().setRealignRate(realignRate);

            return "The realign rate is now set to " + value + " degrees.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    // $ Cloud layer configuration
    private static String setLayerAltitude(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            double altitude = Double.parseDouble(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setAltitude(altitude);
                }
                return "Set altitude of all layers to " + altitude;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setAltitude(altitude);

            // ! CloudLayer.writeCloudLayers();
            return "Set altitude of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getAltitude();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudType(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            CloudConfig.CloudTypes cloudType = CloudConfig.CloudTypes.valueOf(value.toUpperCase());

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudType(cloudType);
                }
                return "Set cloud type of all layers to " + cloudType;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudType(cloudType);

            // ! CloudLayer.writeCloudLayers();
            return "Set cloud type of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudType();
        } catch (IllegalArgumentException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerVerticalRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            float verticalRenderDistance = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setVerticalRenderDistance(verticalRenderDistance);
                }
                return "Set vertical render distance of all layers to " + verticalRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setVerticalRenderDistance(verticalRenderDistance);

            // ! CloudLayer.writeCloudLayers();
            return "Set vertical render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getVerticalRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerHorizontalRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            int horizontalRenderDistance = Integer.parseInt(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setHorizontalRenderDistance(horizontalRenderDistance);
                }
                return "Set horizontal render distance of all layers to " + horizontalRenderDistance + " chunks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setHorizontalRenderDistance(horizontalRenderDistance);

            // ! CloudLayer.writeCloudLayers();
            return "Set horizontal render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getHorizontalRenderDistance() + " chunks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLodRenderDistance(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            float lodRenderDistance = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setLodRenderDistance(lodRenderDistance);
                }
                return "Set LOD render distance of all layers to " + lodRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setLodRenderDistance(lodRenderDistance);

            // ! CloudLayer.writeCloudLayers();
            return "Set LOD render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getLodRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudThickness(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            float cloudThickness = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudThickness(cloudThickness);
                }
                return "Set cloud thickness of all layers to " + cloudThickness + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudThickness(cloudThickness);

            // ! CloudLayer.writeCloudLayers();
            return "Set cloud thickness of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudThickness() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudColor(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            int cloudColor = Integer.parseInt(value,16);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudColor(cloudColor);
                }
                return "Set cloud color of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudColor(cloudColor);

            // ! CloudLayer.writeCloudLayers();
            return "Set cloud color of layer " + layerNumber + " to " + value + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudOpacity(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            float cloudOpacity = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudOpacity(cloudOpacity);
                }
                return "Set cloud opacity of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudOpacity(cloudOpacity);

            // ! CloudLayer.writeCloudLayers();
            return "Set cloud opacity of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].getCloudOpacity() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setShading(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            boolean shading = Boolean.parseBoolean(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setShading(shading);
                }
                return "Set shading of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setShading(shading);

            // ! CloudLayer.writeCloudLayers();
            return "Set shading of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layerNumber-1].isShading() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudSpeed(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            float cloudSpeed = Float.parseFloat(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                    cloudLayer.setCloudSpeed(cloudSpeed);
                }
                return "Set speed of all layers to x" + value + "speed.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudConfig.cloudLayers[layerNumber-1].setCloudSpeed(cloudSpeed);

            // ! CloudLayer.writeCloudLayers();
            return "Set speed of layer " + layerNumber + " to x" + CloudConfig.cloudLayers[layerNumber-1].getCloudSpeed() + " speed.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }
}