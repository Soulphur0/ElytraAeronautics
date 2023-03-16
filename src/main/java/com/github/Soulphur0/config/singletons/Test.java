package com.github.Soulphur0.config.singletons;

import com.github.Soulphur0.config.EanConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.minecraft.client.render.BufferBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class Test {

    // ; Cloud positioning settings
    private boolean useEanClouds;
    private int numberOfLayers;
    private float firstLayerAltitude;
    private float distanceBetweenLayers;

    // ; Cloud rendering settings
    private CloudTypes cloudType;
    private float verticalRenderDistance;
    private int horizontalRenderDistance;
    private float lodRenderDistance;

    // ; Cloud style settings
    private float cloudSpeed;
    private float cloudThickness;
    private int cloudColor;
    private float cloudOpacity;
    private boolean shading;

    // = Instance and layers loaded in memory.
    public static com.github.Soulphur0.config.singletons.CloudLayer instance;
    public static Test2[] cloudLayers;

    public class Test2 implements Serializable {

        // = Cloud layer attributes

        // ; Cloud rendering settings
        @Expose
        private String name;
        @Expose
        private double altitude;
        @Expose
        private CloudTypes cloudType;
        @Expose
        private float verticalRenderDistance;
        @Expose
        private int horizontalRenderDistance;
        @Expose
        private float lodRenderDistance;
        @Expose
        private boolean useSmoothLODs;

        // ; Cloud style settings
        @Expose
        float cloudThickness;
        @Expose
        private double cloudSpeed;
        @Expose
        int cloudColor;
        @Expose
        float cloudOpacity;
        @Expose
        boolean shading;

        // = Contextual attributes for rendering
        private float displacement; // Amount of pixels the texture for clouds will be moved in this layer.
        double renderAltitude; // The 'p' parameter of the original rendering code, it stores the exact render altitude. Which is the remainder of cloud render altitude when measured in cloud thickness.
        BufferBuilder.BuiltBuffer vertexGeometry; // The geometry of the processed cloud layer to later render.
        boolean withinRenderDistance;
        boolean withinLodRenderDistance;
        float translationX;
        float translationZ;

        // $ CLASS METHODS
        // ? Method used get cloud layers from config.
        public static void readConfig(EanConfig... optional){
            EanConfig config;
            try {
                config = optional[0];
            } catch (IndexOutOfBoundsException e){
                config = null;
            }

            // + Load from config screen or directly from file.
            if(config != null){
                // * Save config in the singleton instance.
                instance.setAltitude(config.firstLayerAltitude);

                cloudLayers = new Test2[config.numberOfLayers];

                for (int i = 0; i < config.numberOfLayers; i++) {
                    Test2 layer = new Test2();
                    layer.setName("Layer " + i);
                    layer.setAltitude((config.firstLayerAltitude + config.distanceBetweenLayers * i));
                    //layer.setCloudType(config.cloudType);
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
            } else {
                // ¿ Read cloud layers from file
                StringBuilder json = new StringBuilder();

                try {
                    File file = new File("config/eanCloudLayers.json");
                    Scanner reader = new Scanner(file);

                    while(reader.hasNextLine()){
                        json.append(reader.nextLine());
                    }
                    reader.close();

                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }

                // ¿ Convert extracted string into list then into array.
                Gson gson = new Gson();
                Type type = new TypeToken<List<Test2>>(){}.getType();
                List<Test2> cloudLayers = gson.fromJson(String.valueOf(json), type);

                Test2[] cloudLayerArray = new Test2[cloudLayers.size()];
                for(int i = 0; i< cloudLayers.size(); i++){
                    cloudLayerArray[i] = cloudLayers.get(i);
                }

                // ¿ Load cloud layers into memory.
                com.github.Soulphur0.config.singletons.CloudLayer.CloudLayer.cloudLayers  = cloudLayerArray;
            }

            writeCloudLayers();
        }

        // ? Method used to store cloud layers.
        public static void writeCloudLayers(){
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

        // $ CLOUD PRESETS
        private static void loadPreset_Default(){

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

        public int getCloudColor() {
            return cloudColor;
        }

        public void setCloudColor(int cloudColor) {
            this.cloudColor = cloudColor;
        }

        public boolean isShading() {
            return shading;
        }

        public void setShading(boolean shading) {
            this.shading = shading;
        }

        public double getCloudSpeed() {
            return cloudSpeed;
        }

        public void setCloudSpeed(double cloudSpeed) {
            this.cloudSpeed = cloudSpeed;
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

        public float getCloudOpacity() {
            return cloudOpacity;
        }

        public void setCloudOpacity(float cloudOpacity) {
            this.cloudOpacity = cloudOpacity;
        }
    }

    public enum CloudTypes {
        LOD,
        FAST,
        FANCY
    }

    public enum Attributes {
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