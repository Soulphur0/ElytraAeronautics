package com.github.Soulphur0.registries;

import com.github.Soulphur0.config.cloudlayer.CloudLayer;
import com.github.Soulphur0.config.cloudlayer.CloudLayerAttributes;
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
                                        // * Layer attribute value argument
                                        .then(argument("value", string())
                                                .executes(context -> {
                                                    int layerNumber = IntegerArgumentType.getInteger(context, "layerNumber");
                                                    String layerAttribute = StringArgumentType.getString(context, "layerAttribute");
                                                    String value = StringArgumentType.getString(context, "value");
                                                    String message = "";

                                                    switch (layerAttribute){
                                                        case "altitude":
                                                            message = setLayerAltitude(layerNumber, value);
                                                            break;
                                                        case "cloudType":
                                                            break;
                                                        case "verticalRenderDistance":
                                                            message = setLayerRenderDistance(layerNumber, value);
                                                            break;
                                                        default:
                                                            break;
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
            CloudLayer.cloudLayers[layerNumber].setAltitude(altitude);
            CloudLayer.writeCloudLayers();
            return "Set altitude of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber].getAltitude();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    private static String setLayerRenderDistance(int layerNumber, String value) throws CommandSyntaxException {
        try{
            float verticalRenderDistance = Float.parseFloat(value);
            CloudLayer.cloudLayers[layerNumber].setVerticalRenderDistance(verticalRenderDistance);
            CloudLayer.writeCloudLayers();
            return "Set vertical render distance of layer " + layerNumber + " to " + CloudLayer.cloudLayers[layerNumber].getVerticalRenderDistance();
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

}