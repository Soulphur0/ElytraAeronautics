package com.github.Soulphur0.config;

import java.io.Serializable;

public class CloudLayer implements Serializable {

    private String name; // Cloud layer name given by the system to sort it (Layer 1,  Layer2, ...)
    private float altitude; // Altitude at which the cloud layer will render.
    private float displacement; // Amount of pixels the texture for clouds will be moved in this layer.
    private CloudTypes cloudType; // FAST, FANCY, LOD, more planned for the future.
    private int renderDistance; // Number of layers to skip before rendering this layer
    private boolean useSmoothLODs; // Fast clouds will puff-up gradually.

    public CloudLayer(float altitude, CloudTypes cloudType, boolean useSmoothLODs) {
        this.altitude = altitude;
        this.cloudType = cloudType;
        this.useSmoothLODs = useSmoothLODs;
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
}
