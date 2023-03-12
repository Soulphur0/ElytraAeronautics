package com.github.Soulphur0.config;

import com.github.Soulphur0.config.singletons.CloudLayer;
import com.github.Soulphur0.config.singletons.ElytraFlight;
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
                    String[] suggestions = {"ElytraFlight", "CloudLayer"};

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
                        if (configMode.equals("ElytraFlight")){
                            for(ElytraFlight.Options option : ElytraFlight.Options.values()){
                                suggestions.add(option.toString());
                            }
                        } else if (configMode.equals("CloudLayer")) {
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
                            if (configMode.equals("CloudLayer") && arg1.equals("configCloudLayer")) {
                                suggestions.add("all");
                                for(int i = 1; i <= CloudLayer.cloudLayers.length; i++){
                                    suggestions.add(String.valueOf(i));
                                }
                            }
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        })
                        // = ElytraFlight config execution
                        .executes(context -> {
                            String configMode = StringArgumentType.getString(context, "configMode");
                            String arg1 = StringArgumentType.getString(context, "arg1");
                            String value = StringArgumentType.getString(context, "arg2");
                            String message = "";

                            if (configMode.equals("ElytraFlight")){
                                switch (arg1) {
                                    case "altitudeDeterminesSpeed" -> message = "A value is " + value;
                                    case "minSpeed" -> message = "B value is " + value;
                                    case "maxSpeed" -> message = "C value is " + value;
                                    case "minHeight" -> message = "D value is " + value;
                                    case "maxHeight" -> message = "E value is " + value;
                                    case "sneakingRealignsPitch" -> message = "F value is " + value;
                                    case "realignAngle" -> message = "G value is " + value;
                                    case "realignRate" -> message = "H value is " + value;
                                    default -> {
                                    }
                                }
                            } else if(configMode.equals("CloudLayer") && arg1.equals("layerAmount")) {
                                message = "Cloud layer value is " + value;
                            } else {
                                throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
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
                            if (configMode.equals("CloudLayer")){
                                for(CloudLayer.Attributes attribute : CloudLayer.Attributes.values()){
                                    suggestions.add(attribute.toString());
                                }
                            }
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        })

                        // + Layer attribute value argument
                        .then(argument("value", string())
                            // = CloudLayer config execution
                            .executes(context -> {
                                String configMode = StringArgumentType.getString(context, "configMode");

                                if (configMode.equals("CloudLayer")){
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


    // $ Cloud layer configuration
    private static String setLayerAltitude(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            double altitude = Double.parseDouble(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setAltitude(altitude);
                }
                return "Set altitude of all layers to " + altitude;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setAltitude(altitude);

            CloudLayer.writeCloudLayers();
            return "Set altitude of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getAltitude();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudType(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            CloudLayer.CloudTypes cloudType = CloudLayer.CloudTypes.valueOf(value.toUpperCase());

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setCloudType(cloudType);
                }
                return "Set cloud type of all layers to " + cloudType;
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setCloudType(cloudType);

            CloudLayer.writeCloudLayers();
            return "Set cloud type of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudType();
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setVerticalRenderDistance(verticalRenderDistance);
                }
                return "Set vertical render distance of all layers to " + verticalRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setVerticalRenderDistance(verticalRenderDistance);

            CloudLayer.writeCloudLayers();
            return "Set vertical render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getVerticalRenderDistance() + " blocks.";
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setHorizontalRenderDistance(horizontalRenderDistance);
                }
                return "Set horizontal render distance of all layers to " + horizontalRenderDistance + " chunks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setHorizontalRenderDistance(horizontalRenderDistance);

            CloudLayer.writeCloudLayers();
            return "Set horizontal render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getHorizontalRenderDistance() + " chunks.";
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setLodRenderDistance(lodRenderDistance);
                }
                return "Set LOD render distance of all layers to " + lodRenderDistance + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setLodRenderDistance(lodRenderDistance);

            CloudLayer.writeCloudLayers();
            return "Set LOD render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getLodRenderDistance() + " blocks.";
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setCloudThickness(cloudThickness);
                }
                return "Set cloud thickness of all layers to " + cloudThickness + " blocks.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setCloudThickness(cloudThickness);

            CloudLayer.writeCloudLayers();
            return "Set cloud thickness of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudThickness() + " blocks.";
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setCloudColor(cloudColor);
                }
                return "Set cloud color of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setCloudColor(cloudColor);

            CloudLayer.writeCloudLayers();
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setCloudOpacity(cloudOpacity);
                }
                return "Set cloud opacity of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setCloudOpacity(cloudOpacity);

            CloudLayer.writeCloudLayers();
            return "Set cloud opacity of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudOpacity() + ".";
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
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setShading(shading);
                }
                return "Set shading of all layers to " + value + ".";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setShading(shading);

            CloudLayer.writeCloudLayers();
            return "Set shading of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].isShading() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudSpeed(String layerNumberArg, String value) throws CommandSyntaxException {
        try{
            double cloudSpeed = Double.parseDouble(value);

            if (layerNumberArg.equals("all")){
                for(CloudLayer cloudLayer : CloudLayer.cloudLayers){
                    cloudLayer.setCloudSpeed(cloudSpeed);
                }
                return "Set speed of all layers to x" + value + "speed.";
            }
            int layerNumber = Integer.parseInt(layerNumberArg);
            CloudLayer.cloudLayers[layerNumber-1].setCloudSpeed(cloudSpeed);

            CloudLayer.writeCloudLayers();
            return "Set speed of layer " + layerNumber + " to x" + CloudLayer.cloudLayers[layerNumber-1].getCloudSpeed() + " speed.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }
}