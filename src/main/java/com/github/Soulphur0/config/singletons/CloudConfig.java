package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.clothConfig.CloudConfigScreen;
import com.github.Soulphur0.config.objects.CloudLayer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class CloudConfig {

    // ; Cloud positioning settings
    @Expose
    private int numberOfLayers;
    @Expose
    private float firstLayerAltitude;
    @Expose
    private float distanceBetweenLayers;

    // ; Cloud rendering settings
    @Expose
    private boolean useEanClouds;
    @Expose
    private CloudTypes cloudType;
    @Expose
    private float verticalRenderDistance;
    @Expose
    private int horizontalRenderDistance;
    @Expose
    private float lodRenderDistance;

    // ; Cloud style settings
    @Expose
    private float cloudSpeed;
    @Expose
    private float cloudThickness;
    @Expose
    private int cloudColor;
    @Expose
    private float cloudOpacity;
    @Expose
    private boolean shading;

    // = Config instance
    // ; The singleton instance stores and manages general cloud config data.
    // ; Fields are made private to conditionally write the data directly into the config screen via its setters.
    public static CloudConfig instance;

    // = Layers loaded in memory.
    // ; The static array of cloud layers stores all cloud layers in an easier to access way.
    public static CloudLayer[] cloudLayers;

    // $ CLASS METHODS
    public static CloudConfig getOrCreateInstance(){
        if (instance == null){
            instance = new CloudConfig();
        }
        return instance;
    }

    // ? Method accessed via the ClothConfig save listener
    // ¿ Updates the config instance with the config screen values.
    public static void updateConfig(CloudConfigScreen... optional){
        getOrCreateInstance();
        try {
            CloudConfigScreen config = optional[0];

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

            // Apply settings to each layer.
            cloudLayers = new CloudLayer[config.numberOfLayers];
            for (int i = 0; i < config.numberOfLayers; i++) {
                CloudLayer layer = new CloudLayer();
                layer.setAltitude((config.firstLayerAltitude + config.distanceBetweenLayers * i));
                layer.setCloudType(config.cloudType);
                layer.setVerticalRenderDistance(config.verticalRenderDistance);
                layer.setHorizontalRenderDistance(config.horizontalRenderDistance);
                layer.setLodRenderDistance(config.lodRenderDistance);
                layer.setCloudThickness(config.cloudThickness);
                layer.setCloudColor(config.cloudColor);
                layer.setCloudOpacity(config.cloudOpacity);
                layer.setShading(config.shading);
                layer.setCloudSpeed(config.cloudSpeed);

                cloudLayers[i] = layer;
            }
        } catch (IndexOutOfBoundsException e){
            // - Extract json as string
            StringBuilder json = new StringBuilder();

            try {
                File file = new File("config/elytraAeronautics/eanCloudLayers.json");
                Scanner reader = new Scanner(file);

                while(reader.hasNextLine()){
                    json.append(reader.nextLine());
                }
                reader.close();
            } catch (FileNotFoundException f){
                e.printStackTrace();
            }

            // - Convert extracted string into list of configured CloudLayers.
            Gson gson = new Gson();
            Type type = new TypeToken<List<CloudLayer>>(){}.getType();
            List<CloudLayer> readCloudLayers = gson.fromJson(String.valueOf(json), type);

            // - Load read CloudLayers into the in-memory array.
            cloudLayers = new CloudLayer[readCloudLayers.size()];
            for(int i = 0; i< readCloudLayers.size(); i++){
                cloudLayers[i] = readCloudLayers.get(i);
            }
        }
    }

    public static void writeConfig(){
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(cloudLayers);

        try {
            FileWriter writer = new FileWriter("config/eanCloudLayers.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        SEA_MISTY
    }
}
