/*
package com.github.Soulphur0.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EanConfigFile implements Serializable {
    // Elytra flight
    boolean altitudeDeterminesSpeed;
    private double minSpeed;
    private double maxSpeed;

    private double curveStart;
    private double curveEnd;

    boolean sneakRealignsPitch;
    float realignmentAngle;
    float realignmentRate;

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

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getCurveStart() {
        return curveStart;
    }

    public void setCurveStart(double curveStart) {
        this.curveStart = curveStart;
    }

    public double getCurveEnd() {
        return curveEnd;
    }

    public void setCurveEnd(double curveEnd) {
        this.curveEnd = curveEnd;
    }

    public boolean isSneakRealignsPitch() {
        return sneakRealignsPitch;
    }

    public void setSneakRealignsPitch(boolean sneakRealignsPitch) {
        this.sneakRealignsPitch = sneakRealignsPitch;
    }

    public float getRealignmentRate() {
        return realignmentRate;
    }

    public void setRealignmentRate(float realignmentRate) {
        this.realignmentRate = realignmentRate;
    }

    public float getRealignmentAngle() {
        return realignmentAngle;
    }

    public void setRealignmentAngle(float realignmentAngle) {
        this.realignmentAngle = realignmentAngle;
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

    public boolean isAltitudeDeterminesSpeed() {
        return altitudeDeterminesSpeed;
    }

    public void setAltitudeDeterminesSpeed(boolean altitudeDeterminesSpeed) {
        this.altitudeDeterminesSpeed = altitudeDeterminesSpeed;
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
        altitudeDeterminesSpeed = true;
        minSpeed = 30.35D;
        maxSpeed = 257.22D;
        curveStart = 250.0D;
        curveEnd = 1000.0D;

        sneakRealignsPitch = true;
        realignmentAngle = 0.0F;
        realignmentRate = 0.1F;

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
*/