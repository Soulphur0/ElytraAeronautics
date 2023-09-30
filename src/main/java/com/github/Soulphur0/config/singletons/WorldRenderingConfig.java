package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.EanServerSettings;
import com.github.Soulphur0.config.constants.ChunkUnloadingConditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class WorldRenderingConfig {

    // = Chunk unloading attributes
    @Expose
    private boolean useEanChunkUnloading = true;
    @Expose
    private ChunkUnloadingConditions chunkUnloadingCondition = ChunkUnloadingConditions.SPEED_OR_HEIGHT;
    @Expose
    private double unloadingSpeed = 100.0D;
    @Expose
    private double unloadingHeight = 320.0D;

    // = Config instance
    public static WorldRenderingConfig instance;

    public static WorldRenderingConfig getOrCreateInstance() {
        if (instance == null){
            instance = new WorldRenderingConfig();
        }
        return instance;
    }

    // $ Constructors
    // € These are used within the scope of this class or EanServerSettings, networking is always handled around the latter.

    // ? Without parameters.
    // ¿ Used in the instance initializer method above.
    public WorldRenderingConfig(){}

    // ? With parameters.
    // ¿ Used in the createFromBuffer() method of the ServerSettings class.
    public WorldRenderingConfig(boolean useEanChunkUnloading, ChunkUnloadingConditions chunkUnloadingCondition, double unloadingSpeed,  double unloadingHeight) {
        this.useEanChunkUnloading = useEanChunkUnloading;
        this.chunkUnloadingCondition = chunkUnloadingCondition;
        this.unloadingSpeed = unloadingSpeed;
        this.unloadingHeight = unloadingHeight;
    }

    //. NETWORKING
    // ? Method to update the current singleton instance with the server's.
    // ¿ Called in the EanClientPacketDispatcher class' receive() method. Basically a named setter; although not directly related with networking, labeled as such for clarity purposes.
    public static void updateClientSettings(WorldRenderingConfig worldRenderingConfig){
        instance = worldRenderingConfig;
    }

    // $ Disk read/write

    // ? Reads config values from the config file.
    // ¿ Called on upon joining any world, and whenever configuration is changed.
    public static void readFromDisk() {
        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/world_rendering_settings.json");

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
        instance = gson.fromJson(json.toString(), WorldRenderingConfig.class);
    }

    // ? Writes flight config values to the flight config file.
    // ¿ Called every time the config is updated via command.
    public static void writeToDisk() {
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(instance);

        // Obtain the configuration directory
        Path configDir = FabricLoader.getInstance().getConfigDir();

        // Define the path for the config file within the config directory
        Path configFile = configDir.resolve("ElytraAeronautics/world_rendering_settings.json");

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
    public boolean isUseEanChunkUnloading() {
        return useEanChunkUnloading;
    }

    public void setUseEanChunkUnloading(boolean useEanChunkUnloading) {
        this.useEanChunkUnloading = useEanChunkUnloading;
    }

    public double getUnloadingHeight() {
        return unloadingHeight;
    }

    public void setUnloadingHeight(double unloadingHeight) {
        this.unloadingHeight = unloadingHeight;
    }

    public ChunkUnloadingConditions getChunkUnloadingCondition() {
        return chunkUnloadingCondition;
    }

    public void setChunkUnloadingCondition(ChunkUnloadingConditions chunkUnloadingCondition) {
        this.chunkUnloadingCondition = chunkUnloadingCondition;
    }

    public double getUnloadingSpeed() {
        return unloadingSpeed;
    }

    public void setUnloadingSpeed(double unloadingSpeed) {
        this.unloadingSpeed = unloadingSpeed;
    }
}
