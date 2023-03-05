package com.github.Soulphur0.config.cloudlayer;

import com.github.Soulphur0.behaviour.EanCloudRenderBehaviour;
import com.github.Soulphur0.config.EanConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.render.BufferBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class CloudLayer implements Serializable {

    // = Properties of the layer
    private String name; // Cloud layer name given by the system to sort it (Layer 1,  Layer2, ...)
    private double altitude; // Altitude at which the cloud layer will render.
    private CloudTypes cloudType; // FAST, FANCY, LOD, more planned for the future.
    private float verticalRenderDistance; // Min distance to the layer at which it will render.
    private int horizontalRenderDistance; // Number of chunks the cloud layer occupies.
    private float lodRenderDistance; // Min distance to the layer at which it will render with high LOD.
    private boolean useSmoothLODs; // Fast clouds will puff-up gradually.
    float cloudThickness;

    // = Contextual attributes for rendering
    private float displacement; // Amount of pixels the texture for clouds will be moved in this layer.
    double renderAltitude; // The 'p' parameter of the original rendering code, it stores the exact render altitude. Which is the remainder of cloud render altitude when measured in cloud thickness.
    BufferBuilder.BuiltBuffer vertexGeometry; // The geometry of the processed cloud layer to later render.
    boolean withinRenderDistance;
    boolean withinLodRenderDistance;

    // = Cloud layers
    public static CloudLayer[] cloudLayers;

    public CloudLayer(){

    }

    // $ CLASS METHODS
    // € Used to generate cloud layers and save/load them into storage.

    // ? Method used to set up all cloud layers at once and store them.
    // ¿ Called when saving the config screen menu.
    public static void writeCloudLayers(EanConfig config){
        cloudLayers = new CloudLayer[config.numberOfLayers];

        for (int i = 0; i < config.numberOfLayers; i++) {
            CloudLayer layer = new CloudLayer();
            layer.setName("Layer " + i);
            layer.setAltitude((config.firstLayerAltitude + config.distanceBetweenLayers * i));
            layer.setCloudType(config.cloudType);
            layer.setVerticalRenderDistance(config.verticalRenderDistance);
            layer.setHorizontalRenderDistance(config.horizontalRenderDistance);
            layer.setLodRenderDistance(config.lodRenderDistance);
            layer.setUseSmoothLODs(config.useSmoothLods);
            layer.setCloudThickness(config.cloudThickness);

            cloudLayers[i] = layer;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(cloudLayers);

        try {
            FileWriter writer = new FileWriter("eanCloudLayers.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        config.generateDefaultPreset = false;
        EanCloudRenderBehaviour.configUpdated = false;
    }

    // ? Method used to get the saved cloud layers.
    public static void readCloudLayers(){
        StringBuilder json = new StringBuilder();

        try {
            File file = new File("eanCloudLayers.json");
            Scanner reader = new Scanner(file);

            while(reader.hasNextLine()){
                json.append(reader.nextLine());
            }
            reader.close();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        Gson gson = new Gson();

        Type type = new TypeToken<List<CloudLayer>>(){}.getType();
        List<CloudLayer> cloudLayers = gson.fromJson(String.valueOf(json), type);
    }

    // $ GETTERS & SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public CloudTypes getCloudType() {
        return cloudType;
    }

    public void setCloudType(CloudTypes cloudType) {
        this.cloudType = cloudType;
    }

    public boolean isUseSmoothLODs() {
        return useSmoothLODs;
    }

    public void setUseSmoothLODs(boolean useSmoothLODs) {
        this.useSmoothLODs = useSmoothLODs;
    }

    public float getDisplacement() {
        return displacement;
    }

    public void setDisplacement(float displacement) {
        this.displacement = displacement;
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

    public boolean isWithinRenderDistance() {
        return withinRenderDistance;
    }

    public void setWithinRenderDistance(boolean withinRenderDistance) {
        this.withinRenderDistance = withinRenderDistance;
    }

    public boolean isWithinLodRenderDistance() {
        return withinLodRenderDistance;
    }

    public void setWithinLodRenderDistance(boolean withinLodRenderDistance) {
        this.withinLodRenderDistance = withinLodRenderDistance;
    }

    public double getRenderAltitude() {
        return renderAltitude;
    }

    public void setRenderAltitude(double renderAltitude) {
        this.renderAltitude = renderAltitude;
    }

    public BufferBuilder.BuiltBuffer getVertexGeometry() {
        return vertexGeometry;
    }

    public void setVertexGeometry(BufferBuilder.BuiltBuffer vertexGeometry) {
        this.vertexGeometry = vertexGeometry;
    }

    public float getCloudThickness() {
        return cloudThickness;
    }

    public void setCloudThickness(float cloudThickness) {
        this.cloudThickness = cloudThickness;
    }
}
