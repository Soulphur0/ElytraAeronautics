package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.clothConfig.CloudConfigScreen;
import com.github.Soulphur0.config.objects.CloudLayer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class CloudConfig {

    // ; Cloud positioning settings
    @Expose
    private int numberOfLayers = 2;
    @Expose
    private float firstLayerAltitude = 192.0F;
    @Expose
    private float distanceBetweenLayers = 25.0F;

    // ; Cloud rendering settings
    @Expose
    private boolean useEanClouds = true;
    @Expose
    private CloudTypes cloudType = CloudTypes.LOD;
    @Expose
    private float verticalRenderDistance = 1000.0F;
    @Expose
    private int horizontalRenderDistance = 20;
    @Expose
    private float lodRenderDistance = 50.0F;

    // ; Cloud style settings
    @Expose
    private float cloudSpeed = 1.0F;
    @Expose
    private float cloudThickness = 4.0F;
    @Expose
    private int cloudColor = 0xffffff;
    @Expose
    private float cloudOpacity = 0.8F;
    @Expose
    private boolean shading = true;

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

    // $ ClothConfig config updater.
    // € Updates the config instance with the config screen values, which are automatically saved on disk.
    public static void updateConfig(CloudConfigScreen config){
        getOrCreateInstance();

        // Load general config settings to the singleton instance.
        instance.setNumberOfLayers(config.numberOfLayers);
        instance.setFirstLayerAltitude(config.firstLayerAltitude);
        instance.setDistanceBetweenLayers(config.distanceBetweenLayers);

        instance.setUseEanClouds(config.useEanClouds);
        instance.setCloudType(config.cloudType);
        instance.setVerticalRenderDistance(config.verticalRenderDistance);
        instance.setHorizontalRenderDistance(config.horizontalRenderDistance);
        instance.setLodRenderDistance(config.lodRenderDistance);

        instance.setCloudSpeed(config.cloudSpeed);
        instance.setCloudThickness(config.cloudThickness);
        instance.setCloudColor(config.cloudColor);
        instance.setCloudOpacity(config.cloudOpacity);
        instance.setShading(config.shading);

        // Apply changes to all cloud layers.
        cloudLayers = new CloudLayer[getOrCreateInstance().getNumberOfLayers()];
        for (int i = 0; i < getOrCreateInstance().numberOfLayers; i++) {
            CloudLayer layer = new CloudLayer();
            layer.setAltitude((getOrCreateInstance().firstLayerAltitude + getOrCreateInstance().distanceBetweenLayers * i));
            layer.setCloudType(getOrCreateInstance().cloudType);
            layer.setVerticalRenderDistance(getOrCreateInstance().verticalRenderDistance);
            layer.setHorizontalRenderDistance(getOrCreateInstance().horizontalRenderDistance);
            layer.setLodRenderDistance(getOrCreateInstance().lodRenderDistance);
            layer.setCloudThickness(getOrCreateInstance().cloudThickness);
            layer.setCloudColor(getOrCreateInstance().cloudColor);
            layer.setCloudOpacity(getOrCreateInstance().cloudOpacity);
            layer.setShading(getOrCreateInstance().shading);
            layer.setCloudSpeed(getOrCreateInstance().cloudSpeed);

            cloudLayers[i] = layer;
        }
    }

    // $ Non-ClothConfig config updater
    // € Updates are done via setters in the command methods, where the writeToDisk method is called right after.

    // ? Only called once in mod initialization.
    public static void readFromDisk(){
        // - Extract json as string.
        StringBuilder json = new StringBuilder();
        try {
            // Create dir if it doesn't exist.
            File directory = new File("config/ElytraAeronautics");
            if (!directory.exists())
                directory.mkdir();

            File file = new File("config/ElytraAeronautics/cloud_settings.json");

            // If file doesn't exist yet, create instance with default values and write it to disk.
            // Also, the default cloud preset will be generated to be saved as well.
            if (file.createNewFile()) {
                getOrCreateInstance();
                cloudPreset_default();
                writeToDisk();
            } else {
                Scanner reader = new Scanner(file);
                while(reader.hasNextLine()){
                    json.append(reader.nextLine());
                }
                reader.close();

                // + After reading general config from disk, read the cloud layers into array.
                readCloudLayers();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        // - Convert extracted string into the config instance.
        Gson gson = new Gson();
        instance = gson.fromJson(String.valueOf(json), CloudConfig.class);
    }

    // ? Called every time the config is updated via command.
    public static void writeToDisk(){
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(instance);

        try {
            FileWriter writer = new FileWriter("config/ElytraAeronautics/cloud_settings.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // + After writing general config to disk, write the cloud layers array into JSON.
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
    // € Each method generated a different cloud preset, names registered in enum at the end.
    public static void cloudPreset_default(){
        cloudLayers = new CloudLayer[3];

        cloudLayers[0] = new CloudLayer();
        cloudLayers[0].setAltitude(192.0D);
        cloudLayers[0].setCloudType(CloudTypes.FANCY);

        cloudLayers[1] = new CloudLayer();
        cloudLayers[1].setAltitude(250.0D);

        cloudLayers[2] = new CloudLayer();
        cloudLayers[2].setAltitude(1000.0D);
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

        if (cloudLayers.length > this.numberOfLayers){
            CloudLayer[] aux = new CloudLayer[this.numberOfLayers];
            for (int i = 0; i < this.numberOfLayers; i++){
                aux[i] = cloudLayers[i];
            }
            cloudLayers = aux;
        } else if (cloudLayers.length < this.numberOfLayers){
            CloudLayer[] aux = new CloudLayer[this.numberOfLayers];
            for (int i = 0; i < this.numberOfLayers; i++){
                if (i < cloudLayers.length)
                    aux[i] = cloudLayers[i];
                else
                    aux[i] = new CloudLayer();
            }
            cloudLayers = aux;
        }
    }

    public float getFirstLayerAltitude() {
        return firstLayerAltitude;
    }

    public void setFirstLayerAltitude(float firstLayerAltitude) {
        this.firstLayerAltitude = firstLayerAltitude;
    }

    public float getDistanceBetweenLayers() {
        return distanceBetweenLayers;
    }

    public void setDistanceBetweenLayers(float distanceBetweenLayers) {
        this.distanceBetweenLayers = distanceBetweenLayers;
    }

    public CloudTypes getCloudType() {
        return cloudType;
    }

    public void setCloudType(CloudTypes cloudType) {
        this.cloudType = cloudType;
    }

    public float getVerticalRenderDistance() {
        return verticalRenderDistance;
    }

    public void setVerticalRenderDistance(float verticalRenderDistance) {
        this.verticalRenderDistance = verticalRenderDistance;
    }

    public int getHorizontalRenderDistance() {
        return horizontalRenderDistance;
    }

    public void setHorizontalRenderDistance(int horizontalRenderDistance) {
        this.horizontalRenderDistance = horizontalRenderDistance;
    }

    public float getLodRenderDistance() {
        return lodRenderDistance;
    }

    public void setLodRenderDistance(float lodRenderDistance) {
        this.lodRenderDistance = lodRenderDistance;
    }

    public float getCloudSpeed() {
        return cloudSpeed;
    }

    public void setCloudSpeed(float cloudSpeed) {
        this.cloudSpeed = cloudSpeed;
    }

    public float getCloudThickness() {
        return cloudThickness;
    }

    public void setCloudThickness(float cloudThickness) {
        this.cloudThickness = cloudThickness;
    }

    public int getCloudColor() {
        return cloudColor;
    }

    public void setCloudColor(int cloudColor) {
        this.cloudColor = cloudColor;
    }

    public float getCloudOpacity() {
        return cloudOpacity;
    }

    public void setCloudOpacity(float cloudOpacity) {
        this.cloudOpacity = cloudOpacity;
    }

    public boolean isShading() {
        return shading;
    }

    public void setShading(boolean shading) {
        this.shading = shading;
    }

    public enum CloudTypes {
        LOD,
        FAST,
        FANCY
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
        shading
    }

    public enum Presets {
        DEFAULT,
        PUFFY,
        RAINBOW,
        SKY_HIGHWAY,
        SEA_MIST
    }
}
