package com.github.Soulphur0.config;

import java.io.Serializable;

public class CloudLayer implements Serializable {

    private String name; // Cloud layer name given by the system to sort it (Layer 1,  Layer2, ...)
    private float altitude; // Altitude at which the cloud layer will render.
    private float displacement; // Amount of pixels the texture for clouds will be moved in this layer.
    private CloudTypes cloudType; // FAST, FANCY, LOD, more planned for the future.
    private float verticalRenderDistance; // Min distance to the layer at which it will render.
    private int horizontalRenderDistance; // Number of chunks the cloud layer occupies.
    private float lodRenderDistance; // Min distance to the layer at which it will render with high LOD.
    private boolean useSmoothLODs; // Fast clouds will puff-up gradually.

    public CloudLayer(){

    }

    // $ GETTERS & SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
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
}
