package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.commands.EanCommandsHelp;
import com.github.Soulphur0.config.objects.CloudLayer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class CloudConfig {

    // ; Cloud positioning settings
    @Expose
    private int numberOfLayers = 3;

    // ; Cloud rendering settings
    @Expose
    private boolean useEanClouds = true;

    // = Config instance
    public static CloudConfig instance;

    // = Layers loaded in memory.
    @Expose
    public static CloudLayer[] cloudLayers;

    public static CloudConfig getOrCreateInstance(){
        if (instance == null){
            instance = new CloudConfig();
        }
        return instance;
    }

    // $ Config updater
    // € Updates are done via command, a packet is sent to the client with the new settings.

    // ? Updates a specific setting with the provided value.
    // ¿ Called in the EanClientPacketHandler class' processClientSettings method.
    public static void updateGeneralConfig(MinecraftClient client, PacketByteBuf settingAttributes){
        ClientPlayerEntity player = client.player;

        String settingName = settingAttributes.readString();
        String value = settingAttributes.readString();

        if (player != null){
            switch (settingName){
                case "useEanCloudRendering" -> {
                    // _ Print help if the value was -help.
                    if (value.equals("-help")){
                        player.sendMessage(EanCommandsHelp.useEanCloudRendering());
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
                        player.sendMessage(EanCommandsHelp.setCloudLayerAmount());
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
                        player.sendMessage(EanCommandsHelp.loadPreset());
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

    // ? Updates a specific setting with the provided value.
    // ¿ Called in the EanClientPacketHandler class' processClientSettings method.
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
                        player.sendMessage(EanCommandsHelp.setCloudLayerAmount());
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
                        player.sendMessage(EanCommandsHelp.cloudType());
                        return;
                    }

                    // + Set new config value.
                    String notification;
                    try {
                        CloudConfig.CloudTypes cloudType = CloudConfig.CloudTypes.valueOf(value.toUpperCase());

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
                        player.sendMessage(EanCommandsHelp.verticalRenderDistance());
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
                        player.sendMessage(EanCommandsHelp.horizontalRenderDistance());
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
                        player.sendMessage(EanCommandsHelp.lodRenderDistance());
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
                        player.sendMessage(EanCommandsHelp.thickness());
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
                        player.sendMessage(EanCommandsHelp.color());
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
                        player.sendMessage(EanCommandsHelp.opacity());
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
                        player.sendMessage(EanCommandsHelp.shading());
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
                        player.sendMessage(EanCommandsHelp.speed());
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
                        player.sendMessage(EanCommandsHelp.skyEffects());
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

            writeToDisk();
        }
    }

    // ? Reads cloud config values from the cloud config file.
    // ¿ Only called once in mod initialization.
    public static void readFromDisk() {
        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the cloud config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/cloud_settings.json");

        StringBuilder json = new StringBuilder();
        try {
            // Create dir if it doesn't exist.
            File directory = configFile.getParent().toFile();
            if (!directory.exists())
                directory.mkdir();

            File file = configFile.toFile();

            // If the file doesn't exist yet, create instance with default values and write it to disk.
            // Also, the default cloud preset will be generated to be saved as well.
            if (file.createNewFile()) {
                getOrCreateInstance();
                cloudPreset_default();
                writeToDisk();
            } else {
                FileReader reader = new FileReader(file);
                int character;
                while ((character = reader.read()) != -1) {
                    json.append((char) character);
                }
                reader.close();

                // After reading general config from disk, read the cloud layers into the array.
                readCloudLayers();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert extracted string into the config instance.
        Gson gson = new Gson();
        instance = gson.fromJson(json.toString(), CloudConfig.class);
    }

    // ? Called every time the config is updated via command.
    public static void writeToDisk() {
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(instance);

        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/cloud_settings.json");

        try {
            // Write JSON to the config file
            FileWriter writer = new FileWriter(configFile.toFile());
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // After writing general config to disk, write the cloud layers array into JSON.
        writeCloudLayers();
    }

    // $ Saving-to and loading-from disk all the Cloud Layers.
    // € Always called in conjunction with the methods that read and write the config.

    // ? Reads the cloud layers from disk, if not present, generates the default preset and saves it to disk.
    public static void readCloudLayers(){
        StringBuilder json = new StringBuilder();

        try {
            File file = new File("config/ElytraAeronautics/cloud_layers.json");

            // If only the cloud layers file doesn't exist, create default one.
            if (!file.exists()){
                cloudPreset_default();
                writeCloudLayers();
            }

            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                json.append(reader.nextLine());
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<CloudLayer>>(){}.getType();
        List<CloudLayer> readCloudLayers = gson.fromJson(String.valueOf(json), type);

        // - Load read CloudLayers into the in-memory array.
        cloudLayers = new CloudLayer[readCloudLayers.size()];
        for(int i = 0; i< readCloudLayers.size(); i++){
            cloudLayers[i] = readCloudLayers.get(i);
        }
    }

    // ? Called every time the config is updated via command (by the writeConfig method).
    public static void writeCloudLayers(){
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(cloudLayers);

        try{
            FileWriter writer = new FileWriter("config/ElytraAeronautics/cloud_layers.json");
            writer.write(json);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // $ Cloud preset methods
    // € Each method generates a different cloud preset, names are registered in an enum at the end.
    public static void cloudPreset_default(){
        getOrCreateInstance().setNumberOfLayers(3);
        cloudLayers = new CloudLayer[3];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(250.0D);

        cloudLayers[2] = new CloudLayer();
        cloudLayers[2].setAltitude(1000.0D);
    }

    public static void cloudPreset_denseAndPuffy(){
        getOrCreateInstance().setNumberOfLayers(5);
        cloudLayers = new CloudLayer[5];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(196.0D);
        cloudLayers[1].setCloudType(CloudTypes.FANCY);

        cloudLayers[2] = new CloudLayer();
        cloudLayers[2].setAltitude(200.0D);
        cloudLayers[2].setCloudType(CloudTypes.FANCY);

        cloudLayers[3] = new CloudLayer();
        cloudLayers[3].setAltitude(256.0D);
        cloudLayers[3].setCloudType(CloudTypes.FANCY);

        cloudLayers[4] = new CloudLayer();
        cloudLayers[4].setAltitude(260.0D);
        cloudLayers[4].setCloudType(CloudTypes.FANCY);
    }

    public static void cloudPreset_windy(){
        getOrCreateInstance().setNumberOfLayers(2);
        cloudLayers = new CloudLayer[2];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);
        cloudLayers[0].setCloudSpeed(4.0F);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(242.0D);
        cloudLayers[1].setCloudType(CloudTypes.FAST);
        cloudLayers[1].setCloudSpeed(8.0F);
    }

    public static void cloudPreset_rainbow(){
        getOrCreateInstance().setNumberOfLayers(7);
        cloudLayers = new CloudLayer[7];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FAST);
        cloudLayers[0].setCloudSpeed(1.0F);
        cloudLayers[0].setCloudColor(0xff0000);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(196.0D);
        cloudLayers[1].setCloudType(CloudTypes.FAST);
        cloudLayers[1].setCloudSpeed(2.0F);
        cloudLayers[1].setCloudColor(0xffa500);

        cloudLayers[2] = new CloudLayer();
        cloudLayers[2].setAltitude(200.0D);
        cloudLayers[2].setCloudType(CloudTypes.FAST);
        cloudLayers[2].setCloudSpeed(3.0F);
        cloudLayers[2].setCloudColor(0xffff00);

        cloudLayers[3] = new CloudLayer();
        cloudLayers[3].setAltitude(204.0D);
        cloudLayers[3].setCloudType(CloudTypes.FAST);
        cloudLayers[3].setCloudSpeed(4.0F);
        cloudLayers[3].setCloudColor(0x008000);

        cloudLayers[4] = new CloudLayer();
        cloudLayers[4].setAltitude(208.0D);
        cloudLayers[4].setCloudType(CloudTypes.FAST);
        cloudLayers[4].setCloudSpeed(5.0F);
        cloudLayers[4].setCloudColor(0x0000ff);

        cloudLayers[5] = new CloudLayer();
        cloudLayers[5].setAltitude(212.0D);
        cloudLayers[5].setCloudType(CloudTypes.FAST);
        cloudLayers[5].setCloudSpeed(6.0F);
        cloudLayers[5].setCloudColor(0x4b0082);

        cloudLayers[6] = new CloudLayer();
        cloudLayers[6].setAltitude(216.0D);
        cloudLayers[6].setCloudType(CloudTypes.FAST);
        cloudLayers[6].setCloudSpeed(7.0F);
        cloudLayers[6].setCloudColor(0xee82ee);
    }

    public static void cloudPreset_skyHighway(){
        getOrCreateInstance().setNumberOfLayers(3);
        cloudLayers = new CloudLayer[3];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(200.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);
        cloudLayers[0].setCloudSpeed(8.0F);
        cloudLayers[0].setCloudColor(0x555555);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(220.0D);
        cloudLayers[1].setCloudType(CloudTypes.FAST);
        cloudLayers[1].setCloudSpeed(8.0F);
        cloudLayers[1].setCloudColor(0x555555);

        cloudLayers[2] = new CloudLayer();
        cloudLayers[2].setAltitude(250.0D);
        cloudLayers[2].setCloudType(CloudTypes.FAST);
        cloudLayers[2].setCloudSpeed(64.0F);
        cloudLayers[2].setCloudColor(0xffff00);
        cloudLayers[2].setSkyEffects(false);
    }

    public static void cloudPreset_seaMist(){
        getOrCreateInstance().setNumberOfLayers(2);
        cloudLayers = new CloudLayer[2];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(63.0D);
        cloudLayers[1].setCloudType(CloudTypes.FANCY);
        cloudLayers[1].setCloudThickness(1.0F);
        cloudLayers[1].setCloudOpacity(0.2F);
    }

    // $ GETTERS & SETTERS
    public boolean isUseEanClouds() {
        return useEanClouds;
    }

    public void setUseEanClouds(boolean useEanClouds) {
        this.useEanClouds = useEanClouds;
    }

    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    public void setNumberOfLayers(int numberOfLayers) {
        this.numberOfLayers = numberOfLayers;

        if (cloudLayers == null) return;

        if (cloudLayers.length > this.numberOfLayers){
            CloudLayer[] aux = new CloudLayer[this.numberOfLayers];
            for (int i = 0; i < this.numberOfLayers; i++){
                aux[i] = cloudLayers[i];
            }
            cloudLayers = aux;
        } else if (cloudLayers.length < this.numberOfLayers){
            CloudLayer[] aux = new CloudLayer[this.numberOfLayers];
            for (int i = 0; i < this.numberOfLayers; i++){
                if (i < cloudLayers.length){
                    aux[i] = cloudLayers[i];
                    aux[i].setAltitude(192.0 + 50.0 * i);
                } else {
                    aux[i] = new CloudLayer();
                    aux[i].setAltitude(192.0 + 50.0 * i);
                }
            }
            cloudLayers = aux;
        }
    }

    public enum Options {
        useEanCloudRendering,
        setCloudLayerAmount,
        configCloudLayer,
        loadPreset
    }

    public enum LayerAttributes {
        altitude,
        cloudType,
        verticalRenderDistance,
        horizontalRenderDistance,
        lodRenderDistance,
        thickness,
        speed,
        color,
        opacity,
        shading,
        skyEffects
    }

    public enum CloudTypes {
        LOD,
        FAST,
        FANCY
    }

    public enum Presets {
        DEFAULT,
        DENSE_AND_PUFFY,
        WINDY,
        RAINBOW,
        SKY_HIGHWAY,
        SEA_MIST
    }
}
