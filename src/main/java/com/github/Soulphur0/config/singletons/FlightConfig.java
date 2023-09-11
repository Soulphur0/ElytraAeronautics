package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.EanServerSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FlightConfig {

    // = Elytra flight attributes

    // ; Flight speed settings.
    @Expose
    private boolean altitudeDeterminesSpeed = true;
    @Expose
    private double minSpeed = 30.35;
    @Expose
    private double maxSpeed = 257.22;
    @Expose
    private double minHeight = 250.0;
    @Expose
    private double maxHeight = 1000.0;

    // ; Flight alignment settings.
    @Expose
    private boolean sneakingRealignsPitch = true;
    @Expose
    private float realignAngle = 0.0F;
    @Expose
    private float realignRate = 0.1F;

    // = Config instance
    public static FlightConfig instance;

    public static FlightConfig getOrCreateInstance() {
        if (instance == null){
            instance = new FlightConfig();
        }
        return instance;
    }

    // $ Constructors
    // € These are used within the scope of this class or EanServerSettings, networking is always handled around the latter.

    // ? Without parameters.
    // ¿ Used in the instance initializer method above.
    public FlightConfig(){}

    // ? With parameters.
    // ¿ Used in the createFromBuffer() method of the ServerSettings class.
    public FlightConfig(boolean altitudeDeterminesSpeed, double minSpeed, double maxSpeed, double minHeight, double maxHeight, boolean sneakingRealignsPitch, float realignAngle, float realignRate) {
        this.altitudeDeterminesSpeed = altitudeDeterminesSpeed;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.sneakingRealignsPitch = sneakingRealignsPitch;
        this.realignAngle = realignAngle;
        this.realignRate = realignRate;
    }

    //. NETWORKING
    // ? Method to update the current singleton instance with the server's.
    // ¿ Called in the EanClientPacketDispatcher class' receive() method. Basically a named setter; although not directly related with networking, labeled as such for clarity purposes.
    public static void updateClientSettings(FlightConfig flightConfig){
        instance = flightConfig;
    }

    // $ Disk read/write

    // ? Reads flight config values from the flight config file.
    // ¿ Called on upon joining any world, and whenever configuration is changed.
    public static void readFromDisk() {
        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the cloud config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/elytra_flight_settings.json");

        StringBuilder json = new StringBuilder();
        try {
            // Create dir if it doesn't exist.
            File directory = configFile.getParent().toFile();
            if (!directory.exists())
                directory.mkdir();

            File file = configFile.toFile();

            // If the file doesn't exist yet, create instance with default values and write it to disk.
            if (file.createNewFile()) {
                getOrCreateInstance();
                writeToDisk();
            } else {
                FileReader reader = new FileReader(file);
                int character;
                while ((character = reader.read()) != -1) {
                    json.append((char) character);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert extracted string into the config instance.
        Gson gson = new Gson();
        instance = gson.fromJson(json.toString(), FlightConfig.class);
    }

    // ? Writes flight config values to the flight config file.
    // ¿ Called every time the config is updated via command.
    public static void writeToDisk() {
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(instance);

        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/elytra_flight_settings.json");

        try {
            // Write JSON to the config file
            FileWriter writer = new FileWriter(configFile.toFile());
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EanServerSettings.settingsChanged = true;
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
