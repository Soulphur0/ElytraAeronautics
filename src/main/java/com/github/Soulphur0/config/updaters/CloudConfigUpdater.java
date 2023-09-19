package com.github.Soulphur0.config.updaters;

import com.github.Soulphur0.config.command.EanCommandHelp;
import com.github.Soulphur0.config.objects.CloudLayer;
import com.github.Soulphur0.config.options.CloudTypes;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.networking.client.EanClientPacketDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Updates cloud settings changed via command and received from the server in a custom packet.<br><br>
 *  Made into a separate class from the config singleton in order to keep things ordered.<br><br>
 * @see CloudConfig
 * @see EanClientPacketDispatcher
 * */
public class CloudConfigUpdater {

    // = The following methods update a specific setting with the provided value.
    // ; Called in the EanClientPacketDispatcher class' processClientSettings method.

    public static void updateGeneralConfig(MinecraftClient client, PacketByteBuf settingAttributes){
        ClientPlayerEntity player = client.player;

        String settingName = settingAttributes.readString();
        String value = settingAttributes.readString();

        if (player != null){
            switch (settingName){
                case "useEanCloudRendering" -> {
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.useEanCloudRendering());
                        return;
                    }

                    // _ Validate value
                    if(!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))){
                        player.sendMessage(Text.translatable("command.error.value.boolean").formatted(Formatting.RED));
                        return;
                    }

                    // + Set new config value.
                    boolean useEanCloudRendering = Boolean.parseBoolean(value);
                    CloudConfig.getOrCreateInstance().setUseEanClouds(useEanCloudRendering);

                    // + Notify player.
                    String notification = (useEanCloudRendering) ? "Elytra Aeronautics cloud rendering is now enabled." : "Elytra Aeronautics cloud rendering is now disabled.";
                    player.sendMessage(Text.of(notification));
                }
                case "setCloudLayerAmount" -> {
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.setCloudLayerAmount());
                        return;
                    }

                    // + Set new config value.
                    int amount;
                    try{
                        amount = Integer.parseInt(value);
                        CloudConfig.getOrCreateInstance().setNumberOfLayers(amount);
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    String notification = "Set number of cloud layers to " + amount;
                    player.sendMessage(Text.of(notification));
                }
                case "loadPreset" -> {
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.loadPreset());
                        return;
                    }

                    // + Set new config value.
                    String preset = value.toUpperCase();
                    switch (preset){
                        case "DEFAULT" -> CloudConfig.cloudPreset_default();
                        case "DENSE_AND_PUFFY" -> CloudConfig.cloudPreset_denseAndPuffy();
                        case "WINDY" -> CloudConfig.cloudPreset_windy();
                        case "RAINBOW" -> CloudConfig.cloudPreset_rainbow();
                        case "SKY_HIGHWAY" -> CloudConfig.cloudPreset_skyHighway();
                        case "SEA_MIST" -> CloudConfig.cloudPreset_seaMist();
                    }

                    // + Notify player.
                    String notification = "Loaded the " + value + " cloud preset.";
                    player.sendMessage(Text.of(notification));
                }
            }
        }

        CloudConfig.writeToDisk();
    }

    public static void updateCloudLayerConfig(MinecraftClient client, PacketByteBuf settingAttributes){
        ClientPlayerEntity player = client.player;

        String settingName = settingAttributes.readString();
        String layerNumber = settingAttributes.readString();
        String value = settingAttributes.readString();

        if (player != null){
            switch (settingName){
                case "altitude"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.altitude());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try {
                        double altitude = Double.parseDouble(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setAltitude(altitude);
                            }
                            notification = "Set altitude of all layers to " + altitude;
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setAltitude(altitude);
                            notification = "Set altitude of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getAltitude();
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "cloudType"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.cloudType());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try {
                        CloudTypes cloudType = CloudTypes.valueOf(value.toUpperCase());

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setCloudType(cloudType);
                            }
                            CloudConfig.writeToDisk();
                            notification =  "Set cloud type of all layers to " + cloudType;
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setCloudType(cloudType);

                            CloudConfig.writeToDisk();
                            notification = "Set cloud type of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getCloudType();
                        }

                    } catch (IllegalArgumentException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "verticalRenderDistance"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.verticalRenderDistance());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        float verticalRenderDistance = Float.parseFloat(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setVerticalRenderDistance(verticalRenderDistance);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set vertical render distance of all layers to " + verticalRenderDistance + " blocks.";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setVerticalRenderDistance(verticalRenderDistance);

                            CloudConfig.writeToDisk();
                            notification = "Set vertical render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getVerticalRenderDistance() + " blocks.";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "horizontalRenderDistance"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.horizontalRenderDistance());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        int horizontalRenderDistance = Integer.parseInt(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setHorizontalRenderDistance(horizontalRenderDistance);
                            }
                            CloudConfig.writeToDisk();
                            notification =  "Set horizontal render distance of all layers to " + horizontalRenderDistance + " chunks.";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setHorizontalRenderDistance(horizontalRenderDistance);

                            CloudConfig.writeToDisk();
                            notification = "Set horizontal render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getHorizontalRenderDistance() + " chunks.";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "lodRenderDistance"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.lodRenderDistance());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        float lodRenderDistance = Float.parseFloat(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setLodRenderDistance(lodRenderDistance);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set LOD render distance of all layers to " + lodRenderDistance + " blocks.";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setLodRenderDistance(lodRenderDistance);

                            CloudConfig.writeToDisk();
                            notification = "Set LOD render distance of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getLodRenderDistance() + " blocks.";
                        }

                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "thickness"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.thickness());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        float cloudThickness = Float.parseFloat(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setCloudThickness(cloudThickness);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set cloud thickness of all layers to " + cloudThickness + " blocks.";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setCloudThickness(cloudThickness);

                            CloudConfig.writeToDisk();
                            notification = "Set cloud thickness of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getCloudThickness() + " blocks.";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "color"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.color());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        int cloudColor = Integer.parseInt(value,16);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setCloudColor(cloudColor);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set cloud color of all layers to " + value + ".";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setCloudColor(cloudColor);

                            CloudConfig.writeToDisk();
                            notification = "Set cloud color of layer " + layerNumber + " to " + value + ".";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "opacity"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.opacity());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        float cloudOpacity = Float.parseFloat(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setCloudOpacity(cloudOpacity);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set cloud opacity of all layers to " + value + ".";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setCloudOpacity(cloudOpacity);

                            CloudConfig.writeToDisk();
                            notification = "Set cloud opacity of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].getCloudOpacity() + ".";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "shading"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.shading());
                        return;
                    }

                    // _ Validate value
                    if(!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))){
                        player.sendMessage(Text.translatable("command.error.value.boolean").formatted(Formatting.RED));
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        boolean shading = Boolean.parseBoolean(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setShading(shading);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set shading of all layers to " + value + ".";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setShading(shading);

                            CloudConfig.writeToDisk();
                            notification = "Set shading of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].isShading() + ".";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "speed"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.speed());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        float cloudSpeed = Float.parseFloat(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setCloudSpeed(cloudSpeed);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set speed of all layers to x" + value + " speed.";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setCloudSpeed(cloudSpeed);

                            CloudConfig.writeToDisk();
                            notification = "Set speed of layer " + layerNumber + " to x" + CloudConfig.cloudLayers[layer-1].getCloudSpeed() + " speed.";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
                case "skyEffects"->{
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandHelp.skyEffects());
                        return;
                    }

                    // _ Validate value
                    if(!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))){
                        player.sendMessage(Text.translatable("command.error.value.boolean").formatted(Formatting.RED));
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try{
                        boolean skyEffects = Boolean.parseBoolean(value);

                        if (layerNumber.equals("all")){
                            for(CloudLayer cloudLayer : CloudConfig.cloudLayers){
                                cloudLayer.setSkyEffects(skyEffects);
                            }
                            CloudConfig.writeToDisk();
                            notification = "Set sky effects of all layers to " + value + ".";
                        } else {
                            int layer = Integer.parseInt(layerNumber);
                            CloudConfig.cloudLayers[layer-1].setSkyEffects(skyEffects);

                            CloudConfig.writeToDisk();
                            notification = "Set sky effects of layer " + layerNumber + " to " + CloudConfig.cloudLayers[layer-1].isSkyEffects() + ".";
                        }
                    } catch (NumberFormatException e){
                        player.sendMessage(Text.translatable("command.error.value").formatted(Formatting.RED));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        player.sendMessage(Text.translatable("command.error.layerNumber").formatted(Formatting.RED));
                        return;
                    }

                    // + Notify player.
                    player.sendMessage(Text.of(notification));
                }
            }

            CloudConfig.writeToDisk();
        }
    }
}
