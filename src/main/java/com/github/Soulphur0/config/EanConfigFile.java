package com.github.Soulphur0.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EanConfigFile implements Serializable {
    // Elytra flight
    boolean elytraExtraBehaviour;
    private double speedConstantAdditionalValue;

    private double curveStart;
    private double curveMiddle;
    private double curveEnd;

    // Cloud customization
    private int layerAmount;

    private float layerDistance;
    private float stackingAltitude;

    CloudTypes cloudType;
    CloudRenderModes renderMode;
    CloudRenderModes lodRenderMode;
    boolean useSmoothLODs;

    List<CloudLayer> cloudLayerList = new ArrayList<>();

    public EanConfigFile(){
        defaultPreset();
    }

    // ? Getters and setters
    public int getLayerAmount() {
        return layerAmount;
    }

    public List<CloudLayer> getCloudLayerList() {
        return cloudLayerList;
    }

    public float getLayerDistance() {
        return layerDistance;
    }

    public CloudTypes getCloudType() {
        return cloudType;
    }

    public CloudRenderModes getRenderMode() {
        return renderMode;
    }

    public CloudRenderModes getLodRenderMode() {
        return lodRenderMode;
    }

    public double getSpeedConstantAdditionalValue() {
        return speedConstantAdditionalValue;
    }

    public void setSpeedConstantAdditionalValue(double speedConstantAdditionalValue) {
        this.speedConstantAdditionalValue = speedConstantAdditionalValue;
    }

    public double getCurveStart() {
        return curveStart;
    }

    public void setCurveStart(double curveStart) {
        this.curveStart = curveStart;
    }

    public double getCurveMiddle() {
        return curveMiddle;
    }

    public void setCurveMiddle(double curveMiddle) {
        this.curveMiddle = curveMiddle;
    }

    public double getCurveEnd() {
        return curveEnd;
    }

    public void setCurveEnd(double curveEnd) {
        this.curveEnd = curveEnd;
    }

    public float getStackingAltitude() {
        return stackingAltitude;
    }

    public void setStackingAltitude(float stackingAltitude) {
        this.stackingAltitude = stackingAltitude;
    }

    public void setLayerAmount(int layerAmount) {
        this.layerAmount = layerAmount;
    }

    public void setLayerDistance(float distance){
        this.layerDistance = distance;
    }

    public boolean isUseSmoothLODs() {
        return useSmoothLODs;
    }

    public void setUseSmoothLODs(boolean useSmoothLODs) {
        this.useSmoothLODs = useSmoothLODs;
    }

    public boolean isElytraExtraBehaviour() {
        return elytraExtraBehaviour;
    }

    public void setElytraExtraBehaviour(boolean elytraExtraBehaviour) {
        this.elytraExtraBehaviour = elytraExtraBehaviour;
    }

    // ? General configuration methods (Setters with extra behaviour)
    public void setCloudType(CloudTypes cloudType){
        this.cloudType = cloudType;

        for (CloudLayer layer : cloudLayerList){
            layer.setCloudType(cloudType);
        }
    }

    public void setRenderMode(CloudRenderModes renderMode){
        this.renderMode = renderMode;
        for (CloudLayer layer : cloudLayerList){
            layer.setRenderMode(renderMode);
        }
    }

    public void setLodRenderMode(CloudRenderModes renderMode){
        this.lodRenderMode = renderMode;
        for (CloudLayer layer : cloudLayerList){
            layer.setLodRenderMode(renderMode);
        }
    }

    // ? Preset setup method

    public void defaultPreset(){
        // Elytra flight configuration
        elytraExtraBehaviour = true;
        speedConstantAdditionalValue = 0.0088D;
        curveStart = 0.0D;
        curveMiddle = 250.0D;
        curveEnd = 1000.0D;

        // Cloud configuration
        layerAmount = 2;
        layerDistance = 250.0F;
        stackingAltitude = 192.0F;
        cloudType = CloudTypes.LOD;
        renderMode = CloudRenderModes.ALWAYS_RENDER;
        lodRenderMode = CloudRenderModes.TWO_IN_ADVANCE;
        useSmoothLODs = false;

        cloudLayerList = new ArrayList<>();
        cloudLayerList.add(new CloudLayer(250.0F, CloudTypes.LOD, CloudRenderModes.ALWAYS_RENDER, 0.0F, CloudRenderModes.ONE_IN_ADVANCE, 0.0F, false));
        cloudLayerList.add(new CloudLayer(1000.0F, CloudTypes.LOD, CloudRenderModes.ALWAYS_RENDER, 0.0F, CloudRenderModes.ONE_IN_ADVANCE, 0.0F, false));
    }

    // ? Initialize method
    public static void initializeConfigFile(){
        ConfigFileWriter.createConfigFile(new EanConfigFile());
    }
}
