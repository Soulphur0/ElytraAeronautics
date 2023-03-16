package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.EanConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.minecraft.client.render.BufferBuilder;

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
    protected CloudTypes cloudType;
    @Expose
    protected float verticalRenderDistance;
    @Expose
    protected int horizontalRenderDistance;
    @Expose
    protected float lodRenderDistance;

    // ; Cloud style settings
    @Expose
    protected float cloudSpeed;
    @Expose
    protected float cloudThickness;
    @Expose
    protected int cloudColor;
    @Expose
    protected float cloudOpacity;
    @Expose
    protected boolean shading;

    // = Config instance and layers loaded in memory.
    public static CloudConfig instance;
    public static CloudLayer[] cloudLayers;

    // $ CLASS METHODS
    public static CloudConfig getOrCreateInstance(){
        if (instance == null){
            instance = new CloudConfig();
        }
        return instance;
    }

    // ? If ClothConfig is installed, read the config screen; otherwise read the config file directly.
    // ¿ The config file must exist first, for this reason write will always be called preemptively at mod initiation.
    public void readConfig(EanConfig... optional){
        try {
            EanConfig config = optional[0];

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

    public class CloudLayer extends CloudConfig{
        // ; Cloud positioning settings
        @Expose
        private double altitude;

        // = Contextual attributes for rendering
        private BufferBuilder.BuiltBuffer vertexGeometry; // The geometry of the processed cloud layer to later render.
        private double renderAltitude; // The 'p' parameter of the original rendering code, it stores the exact render altitude. Which is the remainder of cloud render altitude when measured in cloud thickness.
        private float displacement;
        private boolean withinRenderDistance;
        private boolean withinLodRenderDistance;
        private float translationX;
        private float translationZ;

        // $ GETTERS & SETTERS
        @Override
        public CloudTypes getCloudType() {
            return super.getCloudType();
        }

        @Override
        public void setCloudType(CloudTypes cloudType) {
            super.setCloudType(cloudType);
        }

        @Override
        public float getVerticalRenderDistance() {
            return super.getVerticalRenderDistance();
        }

        @Override
        public void setVerticalRenderDistance(float verticalRenderDistance) {
            super.setVerticalRenderDistance(verticalRenderDistance);
        }

        @Override
        public int getHorizontalRenderDistance() {
            return super.getHorizontalRenderDistance();
        }

        @Override
        public void setHorizontalRenderDistance(int horizontalRenderDistance) {
            super.setHorizontalRenderDistance(horizontalRenderDistance);
        }

        @Override
        public float getLodRenderDistance() {
            return super.getLodRenderDistance();
        }

        @Override
        public void setLodRenderDistance(float lodRenderDistance) {
            super.setLodRenderDistance(lodRenderDistance);
        }

        @Override
        public float getCloudSpeed() {
            return super.getCloudSpeed();
        }

        @Override
        public void setCloudSpeed(float cloudSpeed) {
            super.setCloudSpeed(cloudSpeed);
        }

        @Override
        public float getCloudThickness() {
            return super.getCloudThickness();
        }

        @Override
        public void setCloudThickness(float cloudThickness) {
            super.setCloudThickness(cloudThickness);
        }

        @Override
        public int getCloudColor() {
            return super.getCloudColor();
        }

        @Override
        public void setCloudColor(int cloudColor) {
            super.setCloudColor(cloudColor);
        }

        @Override
        public float getCloudOpacity() {
            return super.getCloudOpacity();
        }

        @Override
        public void setCloudOpacity(float cloudOpacity) {
            super.setCloudOpacity(cloudOpacity);
        }

        @Override
        public boolean isShading() {
            return super.isShading();
        }

        @Override
        public void setShading(boolean shading) {
            super.setShading(shading);
        }

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        public BufferBuilder.BuiltBuffer getVertexGeometry() {
            return vertexGeometry;
        }

        public void setVertexGeometry(BufferBuilder.BuiltBuffer vertexGeometry) {
            this.vertexGeometry = vertexGeometry;
        }

        public double getRenderAltitude() {
            return renderAltitude;
        }

        public void setRenderAltitude(double renderAltitude) {
            this.renderAltitude = renderAltitude;
        }

        public float getDisplacement() {
            return displacement;
        }

        public void setDisplacement(float displacement) {
            this.displacement = displacement;
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

        public float getTranslationX() {
            return translationX;
        }

        public void setTranslationX(float translationX) {
            this.translationX = translationX;
        }

        public float getTranslationZ() {
            return translationZ;
        }

        public void setTranslationZ(float translationZ) {
            this.translationZ = translationZ;
        }
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
