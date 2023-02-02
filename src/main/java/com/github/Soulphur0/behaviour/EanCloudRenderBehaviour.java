
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
        float f = 4.0F;
        float g = 0.00390625F;
        float h = 9.765625E-4F;
        float k = (float) MathHelper.floor(x) * 0.00390625F;
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        float ab = (float)Math.floor(y / 4.0D) * 4.0F;
        if (true) {
            // + The following loop determines cloud render distance.
            // * Cloud quadrants are made out 8x8 flat blocks. One quadrant would equal 1 single iteration of this loop.
            // * A flat block is in reality a single pixel of the clouds texture inflated into a cloud.

            // - The value of these variables determine the span in quadrants of cloud rendering in cardinal directions.
            // / They also determine (in quadrants) the region of the clouds texture to render.
            int westToEastSpan; // Lower negative values => greater West render. Higher positive values => greater East render.
            int northToSouthSpan; // Lower negative values => greater North render. Higher positive values => greater South render.

            // ! DEBUG
            float debugDisplacement = 0.0F; // % Adding to the displacement adds towards the EAST
            // ! -----

            // > A texture displacement in pixels should be added in order to not render the same part of the clouds texture on consecutive layers.
            // < Pixel displacement should be done in multiples of 8 (explained below).
            // < It is best to displace the texture in a single direction; the easiest way is towards the EAST.
            // < This is because the displacement value would be added to the drawing position of each vertex.
            // < and for each 8 pixels displace 1 quadrant would be subtracted from the westToEast variable, which determines the square region of the texture to draw (or pick pixels from).
            for(westToEastSpan = -3; westToEastSpan <= 4; ++westToEastSpan) {
                for(northToSouthSpan = -3; northToSouthSpan <= 4; ++northToSouthSpan) {

                    // - The value of these variables determine where to draw clouds in the previously set render distance.
                    // / Lower values make clouds render twice in the same place.
                    // / While higher values put a gap between drawing quadrants.
                    float westToEastDrawPos = (float)(westToEastSpan * 8);
                    float northToSouthDrawPos = (float)(northToSouthSpan * 8);

                    // + Here vertex are drawn into the builder.
                    // * The float values added to the drawing positions of the vertex determine the pixel of the clouds.png texture positions to be drawn.
                    // * Clouds are drawn in square groups of 8x8 pixels (quadrants) [(0,8);(8,8);(8,0);(0,0)].
                    // * Higher or lower values make clouds split or overlap. The size of quadrants are determined by the previous variables.
                    // > If we want to add a displacement to the cloud layer; we first need for the loop variables to be located in that part of the texture before we try to draw said part of the texture.
                    // < Since here we are drawing quadrants, 1 unit up there in the loop is equal to 8 pixels.
                    // < For a displacement of 100 pixels, we would need 100/8 quadrants; that's why displacement should be set in multiples of 8.
                    if (ab > -5.0F) {
                        // The added values to each vertex should be modified all at once and by the same amount; changing a single value makes it draw in the specified way only when looking at the direction of that vertex.
                        builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }

                    if (ab <= 5.0F) {
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + 4.0F - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + 4.0F - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + 4.0F - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + 4.0F - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }

                    int ag;
                    if (westToEastSpan > -1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    if (westToEastSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    if (northToSouthSpan > -1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }

                    if (northToSouthSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 4.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F), (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                        }
                    }
                }
            }
        } else {
            for(int ah = -32; ah < 32; ah += 32) {
                for(int ai = -32; ai < 32; ai += 32) {
                    builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 32)).texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 32)).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 0)).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 0)).texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }

        return builder.end();
//        float k = (float)MathHelper.floor(x) * 0.00390625F;
//        float l = (float)MathHelper.floor(z) * 0.00390625F;
//        float m = (float)color.x;
//        float n = (float)color.y;
//        float o = (float)color.z;
//        float p = m * 0.9F;
//        float q = n * 0.9F;
//        float r = o * 0.9F;
//        float s = m * 0.7F;
//        float t = n * 0.7F;
//        float u = o * 0.7F;
//        float v = m * 0.8F;
//        float w = n * 0.8F;
//        float aa = o * 0.8F;
//        distanceToCam = (float)Math.floor(distanceToCam / 4.0D) * 4.0F;
//
//        // _ Render layers
//        // * RENDER FANCY clouds either if (fancy clouds are enabled and withing render range) or (within high LOD altitude range and maximum LOD render distance).
//        if (layer.getCloudType().equals(CloudTypes.FANCY) && withinRenderDistance || layer.getCloudType().equals(CloudTypes.LOD) && withinLodRenderDistance){
//            for(int ac = MathHelper.floor(-0.125*layer.getDisplacement()-3); ac <= MathHelper.floor(-0.125*layer.getDisplacement()+4); ++ac) {
//                for(int ad = -3; ad <= 4; ++ad) {
//                    float ae = (float)(ac * 8);
//                    float af = (float)(ad * 8);
//
//                    // This renders the bottom face of clouds.
//                    if (distanceToCam > -6.0F) {
//                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                    }
//
//                    // This renders the top face of clouds.
//                    if (distanceToCam <= 5.0F) {
//                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
//                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
//                        builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
//                        builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
//                    }
//
//                    int aj;
//                    // This renders the left face of clouds.
//                    // Horizontal displacement is added to the if statement to properly cull the west face of clouds.
//                    if (ac > -1 - layer.getDisplacement()) {
//                        for(aj = 0; aj < 8; ++aj) {
//                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//                        }
//                    }
//
//                    if (ac <= 1) {
//                        // This renders the right face of clouds.
//                        for(aj = 0; aj < 8; ++aj) {
//                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//                            builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//                        }
//                    }
//                    // This renders the front(north) face of clouds.
//                    if (ad > -1) {
//                        for(aj = 0; aj < 8; ++aj) {
//                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//                        }
//                    }
//                    // This renders the back(south) face of clouds.
//                    if (ad <= 1) {
//                        for(aj = 0; aj < 8; ++aj) {
//                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//                            builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//                            builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//                        }
//                    }
//                }
//            }
//        }
//        // * RENDER FAST clouds either if (fast clouds are enabled and withing render range) or (within maximum LOD render distance).
//        else if (layer.getCloudType().equals(CloudTypes.FAST) && withinRenderDistance || layer.getCloudType().equals(CloudTypes.LOD) && withinRenderDistance) {
//            for(int ac = MathHelper.floor(-0.125*layer.getDisplacement()-3); ac <= MathHelper.floor(-0.125*layer.getDisplacement()+4); ++ac) {
//                for(int ad = -3; ad <= 4; ++ad) {
//                    float ae = (float) (ac * 8);
//                    float af = (float) (ad * 8);
//                    builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                    builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                    builder.vertex(ae + 8.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                    builder.vertex(ae + 0.0F + layer.getDisplacement(), distanceToCam + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
//                }
//            }
//        }
//
//        return builder.end();
    }
}