package com.github.Soulphur0.config;

import java.io.Serializable;

public class CloudLayer implements Serializable {
    private String name;
    private float altitude;
    private CloudTypes cloudType;

    private CloudRenderModes renderMode;
    private float cloudRenderDistance;

    private CloudRenderModes lodRenderMode;
    private float lodRenderDistance;
    private boolean useSmoothLODs;

    // * Constructors
    public CloudLayer(float altitude, CloudTypes cloudType, CloudRenderModes renderMode, float cloudRenderDistance, CloudRenderModes lodRenderMode, float lodRenderDistance, boolean useSmoothLODs) {
        this.altitude = altitude;
        this.cloudType = cloudType;
        this.renderMode = renderMode;
        this.cloudRenderDistance = cloudRenderDistance;
        this.lodRenderMode = lodRenderMode;
        this.lodRenderDistance = lodRenderDistance;
        this.useSmoothLODs = useSmoothLODs;
    }

    // * Instance methods
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

    public CloudRenderModes getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(CloudRenderModes renderMode) {
        this.renderMode = renderMode;
    }

    public float getCloudRenderDistance() {
        return cloudRenderDistance;
    }

    public void setCloudRenderDistance(float cloudRenderDistance) {
        this.cloudRenderDistance = cloudRenderDistance;
    }

    public CloudRenderModes getLodRenderMode() {
        return lodRenderMode;
    }

    public void setLodRenderMode(CloudRenderModes lodRenderMode) {
        this.lodRenderMode = lodRenderMode;
    }

    public float getLodRenderDistance() {
        return lodRenderDistance;
    }

    public void setLodRenderDistance(float lodRenderDistance) {
        this.lodRenderDistance = lodRenderDistance;
    }

    public boolean isUseSmoothLODs() {
        return useSmoothLODs;
    }

    public void setUseSmoothLODs(boolean useSmoothLODs) {
        this.useSmoothLODs = useSmoothLODs;
    }
}
