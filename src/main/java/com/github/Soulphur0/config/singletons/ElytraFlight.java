package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.EanConfig;

public class ElytraFlight {

    private boolean altitudeDeterminesSpeed;
    private double minSpeed;
    private double maxSpeed;
    private double minHeight;
    private double maxHeight;

    private boolean sneakingRealignsPitch;
    private float realignAngle;
    private float realignRate;

    private static ElytraFlight instance;

    public ElytraFlight(){

    }

    public static ElytraFlight getInstance() {
        return instance;
    }

    public static void refresh(EanConfig config){
        instance.setAltitudeDeterminesSpeed(config.altitudeDeterminesSpeed);
        instance.setMinSpeed(config.minSpeed);
        instance.setMaxSpeed(config.maxSpeed);
        instance.setMinHeight(config.minHeight);
        instance.setMaxHeight(config.maxHeight);

        instance.setSneakingRealignsPitch(config.sneakingRealignsPitch);
        instance.setRealignAngle(config.realignAngle);
        instance.setRealignRate(config.realignRate);
    }

    // $ GETTERS & SETTERS
    public boolean isAltitudeDeterminesSpeed() {
        return altitudeDeterminesSpeed;
    }

    public void setAltitudeDeterminesSpeed(boolean altitudeDeterminesSpeed) {
        this.altitudeDeterminesSpeed = altitudeDeterminesSpeed;
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

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public boolean isSneakingRealignsPitch() {
        return sneakingRealignsPitch;
    }

    public void setSneakingRealignsPitch(boolean sneakingRealignsPitch) {
        this.sneakingRealignsPitch = sneakingRealignsPitch;
    }

    public float getRealignAngle() {
        return realignAngle;
    }

    public void setRealignAngle(float realignAngle) {
        this.realignAngle = realignAngle;
    }

    public float getRealignRate() {
        return realignRate;
    }

    public void setRealignRate(float realignRate) {
        this.realignRate = realignRate;
    }

    public enum Options {
        altitudeDeterminesSpeed,
        minSpeed,
        maxSpeed,
        minHeight,
        maxHeight,
        sneakingRealignsPitch,
        realignAngle,
        realignRate
    }
}
