
package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.CloudLayer;
import com.github.Soulphur0.config.CloudTypes;
import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.utility.EanMath;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EanCloudRenderBehaviour {

    static double x, y, z;
    static Vec3d color;

    // : Cloud rendering entry point for modification.
    public static BufferBuilder.BuiltBuffer ean_renderCloudLayers(BufferBuilder builder, double camY, double capturedX, double capturedY, double capturedZ, Vec3d capturedColor){
        x = capturedX;
        y = capturedY;
        z = capturedZ;
        color = capturedColor;

        return ean_loadCloudRenderConfig(builder, camY);
    }

    // : Setup for cloud rendering.
    static EanConfig config = AutoConfig.getConfigHolder(EanConfig.class).getConfig();

    // ? Read config & retrieve result with said configs.
    private static BufferBuilder.BuiltBuffer ean_loadCloudRenderConfig(BufferBuilder builder, double camY){
        // * Read config. Once per save action.
        if (ElytraAeronautics.readConfigFileCue_WorldRendererMixin){
            config = AutoConfig.getConfigHolder(EanConfig.class).getConfig();
            ElytraAeronautics.readConfigFileCue_WorldRendererMixin = false;
        }

        // * Return buffer with all cloud layers drawn as the config specifies.
        return ean_setCloudRenderShader(builder, camY);
    }

    // : Cloud rendering.
    // FIXME There are two current issues regarding this method:
    //  Cloud configuration is not working as intended.
    //  The bottom face of clouds doesn't render when looking at it from the lower half of the cloud inside the cloud.
    //  When using smooth LODs, clouds sometimes pop-in at full size before returning flat and starting to puff up.
    private static BufferBuilder.BuiltBuffer ean_setCloudRenderShader(BufferBuilder builder, double camY){
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        //float camDistToLayer = (float)Math.floor(camDistY / 4.0D) * 4.0F; // = playerRelativeDistanceFromCloudLayer. Unmodified original variable. Measured in cloud thickness. Might not be necessary.

        return ean_setCloudRenderValues(builder, camY);
    }

    // : Individual layer configuration for rendering.
    private static BufferBuilder.BuiltBuffer ean_setCloudRenderValues(BufferBuilder builder, double camY) {
        // + Set up values for each layer
        for (int i = 1; i<=config.numberOfLayers; i++){

            // % Setup config values for the layer.
            CloudLayer layer = new CloudLayer(); // Create layer
            float layerAltitude = (i == 1) ? config.firstLayerAltitude : i * config.distanceBetweenLayers; // Place clouds vertically
            layer.setAltitude(layerAltitude);
            layer.setDisplacement(i * 100); // Displace clouds horizontally
            layer.setCloudType(config.cloudType); // Set cloud type
            layer.setVerticalRenderDistance(config.verticalRenderDistance); // Set vertical render distance
            layer.setHorizontalRenderDistance(config.horizontalRenderDistance); // Set horizontal render distance
            layer.setLodRenderDistance(config.lodRenderDistance); // Set vertical LOD render distance
            layer.setUseSmoothLODs(config.useSmoothLods); // Set use smooth LODs

            // % Calc parameters to render clouds.
            boolean withinRenderDistance = layer.getVerticalRenderDistance() - camY <= 0;
            boolean withinLodRenderDistance = layer.getLodRenderDistance() - camY <= 0;

            // ? Puff-up clouds.
            float cloudThickness = 4.0F;
            if (config.cloudType.equals(CloudTypes.LOD)){
                // + Sets puff-up start altitude.
                float puffUpStartAltitude = layer.getLodRenderDistance();

                // + Puff-up clouds gradually or suddenly.
                if (config.useSmoothLods)
                    cloudThickness = Math.min((float) EanMath.getLinealValue(puffUpStartAltitude, 0, layer.getAltitude(), 4, camY), 4.0F);
                else
                    cloudThickness =  (layer.getLodRenderDistance() - camY <= 0) ?  4.0F : 0.0F;
            }

            // ? Since the drawing of clouds is rendered relative to the camera;
            float distanceToCam = (float) (layer.getAltitude() - camY + 0.33F);


            return ean_renderCloudLayer(builder, layer, withinRenderDistance, withinLodRenderDistance, distanceToCam, cloudThickness);
        }

        // TODO: Find a proper way to handle empty cloud layer lists.
        //  playerRelativeDistance to cloud layer is the captured Y value, but, that is the distance to the fixed vanilla layer, so I have to handle this for every custom layer.
        return ean_renderCloudLayer(builder, new CloudLayer(), false, false, -7.0F, 0.0F);
    }

    private static BufferBuilder.BuiltBuffer ean_renderCloudLayer(BufferBuilder builder, CloudLayer layer, boolean withinRenderDistance, boolean withinLodRenderDistance, float distanceToCam, float cloudThickness){
        float k = (float)MathHelper.floor(x) * 0.00390625F;
        float l = (float)MathHelper.floor(z) * 0.00390625F;
        float m = (float)color.x;
        float n = (float)color.y;
        float o = (float)color.z;
        float p = m * 0.9F;
        float q = n * 0.9F;
        float r = o * 0.9F;
        float s = m * 0.7F;
        float t = n * 0.7F;
        float u = o * 0.7F;
        float v = m * 0.8F;
        float w = n * 0.8F;
        float aa = o * 0.8F;
        distanceToCam = (float)Math.floor(distanceToCam / 4.0D) * 4.0F;

        // _ Render layers
        // * RENDER FANCY clouds either if (fancy clouds are enabled and withing render range) or (within high LOD altitude range and maximum LOD render distance).
        if (layer.getCloudType().equals(CloudTypes.FANCY) && withinRenderDistance || layer.getCloudType().equals(CloudTypes.LOD) && withinLodRenderDistance){
            for(int ac = MathHelper.floor(-0.125*layer.getDisplacement()-3); ac <= MathHelper.floor(-0.125*layer.getDisplacement()+4); ++ac) {
                for(int ad = -3; ad <= 4; ++ad) {
                    float ae = (float)(ac * 8);
                    float af = (float)(ad * 8);

                    // This renders the bottom face of clouds.
                    if (distanceToCam > -6.0F) {
                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }

                    // This renders the top face of clouds.
                    if (distanceToCam <= 5.0F) {
                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }

                    int aj;
                    // This renders the left face of clouds.
                    // Horizontal displacement is added to the if statement to properly cull the west face of clouds.
                    if (ac > -1 - layer.getDisplacement()) {
                        for(aj = 0; aj < 8; ++aj) {
                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    if (ac <= 1) {
                        // This renders the right face of clouds.
                        for(aj = 0; aj < 8; ++aj) {
                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }
                    // This renders the front(north) face of clouds.
                    if (ad > -1) {
                        for(aj = 0; aj < 8; ++aj) {
                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }
                    // This renders the back(south) face of clouds.
                    if (ad <= 1) {
                        for(aj = 0; aj < 8; ++aj) {
                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                        }
                    }
                }
            }
        }
        // * RENDER FAST clouds either if (fast clouds are enabled and withing render range) or (within maximum LOD render distance).
        else if (layer.getCloudType().equals(CloudTypes.FAST) && withinRenderDistance || layer.getCloudType().equals(CloudTypes.LOD) && withinRenderDistance) {
            for(int ac = MathHelper.floor(-0.125*layer.getDisplacement()-3); ac <= MathHelper.floor(-0.125*layer.getDisplacement()+4); ++ac) {
                for(int ad = -3; ad <= 4; ++ad) {
                    float ae = (float) (ac * 8);
                    float af = (float) (ad * 8);
                    builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }

        return builder.end();
    }
}