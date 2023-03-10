package com.github.Soulphur0.registries;

import com.github.Soulphur0.config.cloudlayer.CloudLayer;
import com.github.Soulphur0.config.cloudlayer.CloudLayerAttributes;
import com.github.Soulphur0.config.cloudlayer.CloudTypes;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ElytraAeronauticsCommands {

    public static void register(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("ean")
                // $ Cloud layer config command
                .then(literal("configCloudLayer")
                        // + Layer number argument
                        .then(argument("layerNumber", integer())
                                // + Layer attribute name argument
                                .then(argument("layerAttribute", string())
                                        // - Cloud attributes suggestions
                                        .suggests(((commandContext, suggestionsBuilder) -> {
                                            Collection<String> suggestions = new ArrayList<>();
                                            for(CloudLayerAttributes attribute : CloudLayerAttributes.values()){
                                                suggestions.add(attribute.toString());
                                            }
                                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                                        }))
                                        // + Layer attribute value argument
                                        .then(argument("value", string())
                                                .executes(context -> {
                                                    int layerNumber = IntegerArgumentType.getInteger(context, "layerNumber");
                                                    String layerAttribute = StringArgumentType.getString(context, "layerAttribute");
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
                                                        case "shading" -> message = setShading(layerNumber, value);
                                                        default -> {
                                                        }
                                                    }

                                                    context.getSource().sendFeedback(Text.of(message));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        ));
    }

    private static String setLayerAltitude(int layerNumber, String value) throws CommandSyntaxException {
        try{
            double altitude = Double.parseDouble(value);
            CloudLayer.cloudLayers[layerNumber-1].setAltitude(altitude);
            CloudLayer.writeCloudLayers();
            return "Set altitude of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getAltitude();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudType(int layerNumber, String value) throws CommandSyntaxException {
        try{
            CloudTypes cloudType = CloudTypes.valueOf(value.toUpperCase());
            CloudLayer.cloudLayers[layerNumber-1].setCloudType(cloudType);
            CloudLayer.writeCloudLayers();
            return "Set cloud type of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudType();
        } catch (IllegalArgumentException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerVerticalRenderDistance(int layerNumber, String value) throws CommandSyntaxException {
        try{
            float verticalRenderDistance = Float.parseFloat(value);
            CloudLayer.cloudLayers[layerNumber-1].setVerticalRenderDistance(verticalRenderDistance);
            CloudLayer.writeCloudLayers();
            return "Set vertical render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getVerticalRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerHorizontalRenderDistance(int layerNumber, String value) throws CommandSyntaxException {
        try{
            int horizontalRenderDistance = Integer.parseInt(value);
            CloudLayer.cloudLayers[layerNumber-1].setHorizontalRenderDistance(horizontalRenderDistance);
            CloudLayer.writeCloudLayers();
            return "Set horizontal render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getHorizontalRenderDistance() + " chunks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLodRenderDistance(int layerNumber, String value) throws CommandSyntaxException {
        try{
            float lodRenderDistance = Float.parseFloat(value);
            CloudLayer.cloudLayers[layerNumber-1].setLodRenderDistance(lodRenderDistance);
            CloudLayer.writeCloudLayers();
            return "Set LOD render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getLodRenderDistance() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setLayerCloudThickness(int layerNumber, String value) throws CommandSyntaxException {
        try{
            float cloudThickness = Float.parseFloat(value);
            CloudLayer.cloudLayers[layerNumber-1].setCloudThickness(cloudThickness);
            CloudLayer.writeCloudLayers();
            return "Set cloud thickness of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudThickness() + " blocks.";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setCloudColor(int layerNumber, String value) throws CommandSyntaxException {
        try{
            int cloudColor = Integer.parseInt(value,16);
            CloudLayer.cloudLayers[layerNumber-1].setCloudColor(cloudColor);
            CloudLayer.writeCloudLayers();
            return "Set cloud color of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].getCloudColor() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }

    private static String setShading(int layerNumber, String value) throws CommandSyntaxException {
        try{
            boolean shading = Boolean.parseBoolean(value);
            CloudLayer.cloudLayers[layerNumber-1].setShading(shading);
            CloudLayer.writeCloudLayers();
            return "Set shading of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber-1].isShading() + ".";
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        } catch (IndexOutOfBoundsException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.layerNumber")).create();
        }
    }
}