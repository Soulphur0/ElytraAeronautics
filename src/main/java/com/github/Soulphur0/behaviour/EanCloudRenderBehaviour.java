
package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.CloudLayer;
import com.github.Soulphur0.config.CloudTypes;
import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.mixin.WorldRendererAccessors;
import com.github.Soulphur0.utility.EanMath;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class EanCloudRenderBehaviour {

    // $ Variables
    private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");

    // : Cloud rendering setup and execution
    public static void ean_renderClouds(WorldRenderer instance, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double camPosX, double camPosY, double camPosZ){
        WorldRendererAccessors worldRenderer = ((WorldRendererAccessors)instance);

        float vanillaCloudHeight = worldRenderer.getWorld().getDimensionEffects().getCloudsHeight();

        if (!Float.isNaN(vanillaCloudHeight)) {
            // + Cloud rendering parameters.
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);

            // + Cloud rendering values.
            float h = 12.0F;
            float i = 4.0F;
            double j = 2.0E-4D;
            double k = (double) (((float) worldRenderer.getTicks() + tickDelta) * 0.03F);
            double l = (camPosX + k) / 12.0D;

            // double cloudRenderAltitude; //(double) (vanillaCloudHeight - (float) camPosY + 0.33F);

            double n = camPosZ / 12.0D + 0.33000001311302185D;
            l -= (double) (MathHelper.floor(l / 2048.0D) * 2048);
            n -= (double) (MathHelper.floor(n / 2048.0D) * 2048);
            float o = (float) (l - (double) MathHelper.floor(l));

            // < This parameter is the value of the cloud render altitude divided by 4,
            // < minus that same very value floored multiplied by 4.
            // <
            // < So it is 4 times between the cloud layer altitude minus the remaining altitude until it
            // < reaches the closest whole number (downwards).
            // float p; // (float) (cloudRenderAltitude / PARAMETER_DEBUG - (double) MathHelper.floor(cloudRenderAltitude / PARAMETER_DEBUG)) * PARAMETER_DEBUG;
            float q = (float) (n - (double) MathHelper.floor(n));

            // + Clear WorldRenderer's cloud buffer.
            if (worldRenderer.getCloudsDirty()) {
                worldRenderer.setCloudsDirty(false);
                if (worldRenderer.getCloudsBuffer() != null) {
                    worldRenderer.getCloudsBuffer().close();
                }
            }

            // = Load config
            EanConfig config = AutoConfig.getConfigHolder(EanConfig.class).getConfig();

            // = Create arrays to store info for each layer.
            BufferBuilder.BuiltBuffer[] layerGeometries = new BufferBuilder.BuiltBuffer[config.numberOfLayers]; // This array stores the geometry for each cloud layer built.
            float[] p = new float[config.numberOfLayers]; // This array stores the remainder of cloud render altitude measured in cloud thickness.

            // + Build geometry for each cloud layer.
            // * The geometry for each layer is built using its own parameters, and is stored in an array.
            // * All values besides the geometry that are important for rendering are stored in an array too.
            for (int layerNum = 0; layerNum < config.numberOfLayers; layerNum++) {
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                Vec3d vec3d = worldRenderer.getWorld().getCloudsColor(tickDelta);

                // - Get settings from the config file.
                double cloudRenderAltitude = (double) ((config.firstLayerAltitude + config.distanceBetweenLayers * layerNum) - (float) camPosY + 0.33F);
                float cloudThickness = config.cloudThickness;

                // - Get the exact render altitude of the layer and store it in the altitudes array.
                p[layerNum] = (float) (cloudRenderAltitude / cloudThickness - (double) MathHelper.floor(cloudRenderAltitude / cloudThickness)) * cloudThickness;

                // - Build the geometry of the cloud layer and store it into the geometries array.
                worldRenderer.setCloudsBuffer(new VertexBuffer());
                BufferBuilder.BuiltBuffer builtBuffer = ean_renderCloudLayers(config, layerNum, bufferBuilder, l, cloudRenderAltitude, n, vec3d); // > Cloud rendering entry
                layerGeometries[layerNum] = builtBuffer;

                // TODO Find out what these parameters do...
                int r = (int) Math.floor(l);
                int s = (int) Math.floor(cloudRenderAltitude / cloudThickness);
                int t = (int) Math.floor(n);

                // ? Mark clouds as dirty
                if (r != worldRenderer.getLastCloudsBlockX() || s != worldRenderer.getLastCloudsBlockY() || t != worldRenderer.getLastCloudsBlockZ() || worldRenderer.getClient().options.getCloudRenderModeValue() != worldRenderer.getLastCloudRenderMode() || worldRenderer.getLastCloudsColor().squaredDistanceTo(vec3d) > 2.0E-4D) {
                    worldRenderer.setLastCloudsBlockX(r);
                    worldRenderer.setLastCloudsBlockY(s);
                    worldRenderer.setLastCloudsBlockZ(t);
                    worldRenderer.setLastCloudsColor(vec3d);
                    worldRenderer.setLastCloudRenderMode(worldRenderer.getClient().options.getCloudRenderModeValue());
                    worldRenderer.setCloudsDirty(true);
                }
            }

            // + Render cloud geometry.
            // * Using the previously generated arrays, clouds are rendered with their own settings.
            for (int builderNum = 0; builderNum<layerGeometries.length; builderNum++) {

                if (layerGeometries[builderNum] !=null){ // > Added
                    worldRenderer.getCloudsBuffer().bind();
                    worldRenderer.getCloudsBuffer().upload(layerGeometries[builderNum]);
                    VertexBuffer.unbind();

                    // * Get shader, texture and background to draw with cloud geometry.
                    RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
                    RenderSystem.setShaderTexture(0, CLOUDS);
                    BackgroundRenderer.setFogBlack();

                    // * Scale cloud geometry to cloud size and translate it.
                    matrices.push();
                    matrices.scale(12.0F, 1.0F, 12.0F);

                    matrices.translate(-o, p[builderNum], -q);

                    // * Render clouds
                    if (worldRenderer.getCloudsBuffer() != null) {
                        worldRenderer.getCloudsBuffer().bind();
                        int u = worldRenderer.getLastCloudRenderMode() == CloudRenderMode.FANCY ? 0 : 1;

                        for (int v = u; v < 2; ++v) {
                            if (v == 0) {
                                RenderSystem.colorMask(false, false, false, false);
                            } else {
                                RenderSystem.colorMask(true, true, true, true);
                            }

                            ShaderProgram shaderProgram = RenderSystem.getShader();
                            worldRenderer.getCloudsBuffer().draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                        }

                        VertexBuffer.unbind();
                    }

                    // * Finish cloud rendering.
                    matrices.pop();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableCull();
                    RenderSystem.disableBlend();
                }
            }
        }
    }

    // : Individual layer configuration for rendering.
    private static BufferBuilder.BuiltBuffer ean_renderCloudLayers(EanConfig config, int layerNum, BufferBuilder bufferBuilder, double camX, double camY, double camZ, Vec3d color) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        // + Setup config values for the layer.
        CloudLayer layer = new CloudLayer(); // Create layer
        layer.setName("Layer " + layerNum); // Give name to the layer
        layer.setAltitude(config.firstLayerAltitude + config.distanceBetweenLayers * (layerNum-1)); // Place clouds vertically
        layer.setDisplacement(layerNum * 100); // Displace clouds horizontally
        layer.setCloudType(config.cloudType); // Set cloud type
        layer.setVerticalRenderDistance(config.verticalRenderDistance); // Set vertical render distance
        layer.setHorizontalRenderDistance(config.horizontalRenderDistance); // Set horizontal render distance
        layer.setLodRenderDistance(config.lodRenderDistance); // Set vertical LOD render distance
        layer.setUseSmoothLODs(config.useSmoothLods); // Set use smooth LODs

        // + Calc parameters on how to, or whether to render or not, the cloud layer.
        boolean withinRenderDistance = layer.getVerticalRenderDistance() - camY <= 0;
        boolean withinLodRenderDistance = layer.getLodRenderDistance() - camY <= 0;

        // + Puff-up clouds.
        float cloudThickness = 4.0F;
        if (config.cloudType.equals(CloudTypes.LOD)){
            // * Sets puff-up start altitude.
            float puffUpStartAltitude = layer.getLodRenderDistance();

            // * Puff-up clouds gradually or suddenly.
            if (config.useSmoothLods)
                cloudThickness = Math.min((float) EanMath.getLinealValue(puffUpStartAltitude, 0, layer.getAltitude(), 4, camY), 4.0F);
            else
                cloudThickness =  (layer.getLodRenderDistance() - camY <= 0) ?  4.0F : 0.0F;
        }

        // ? Since the drawing of clouds is rendered relative to the camera;
        double distanceToCam = camY;// + layerNum * config.distanceBetweenLayers * 4.0;// + config.distanceBetweenLayers * (i-1);

        // > Draw cloud layer into the buffer
        ean_renderCloudLayer(bufferBuilder, camX, camY, camZ, color, layer, withinRenderDistance, withinLodRenderDistance, distanceToCam, cloudThickness);


        // > Return built buffer
        return bufferBuilder.end();
    }

    private static void ean_renderCloudLayer(BufferBuilder builder, double x, double y, double z, Vec3d color, CloudLayer layer, boolean withinRenderDistance, boolean withinLodRenderDistance, double distanceToCam, float cloudThickness){
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

        float ab = (float)Math.floor(distanceToCam / cloudThickness) * cloudThickness; // (float)Math.floor(y / 4.0D) * 4.0F;

        if (layer.getCloudType().equals(CloudTypes.FANCY)) {
            // + The following loop determines cloud render distance.
            // * Cloud quadrants are made out 8x8 flat blocks. One quadrant would equal 1 single iteration of this loop.
            // * A flat block is in reality a single pixel of the clouds texture inflated into a cloud.

            // - The value of these variables determine the span in quadrants of cloud rendering in cardinal directions.
            // / They also determine (in quadrants) the region of the clouds texture to render.
            int westToEastSpan; // Lower negative values => greater West render. Higher positive values => greater East render.
            int northToSouthSpan; // Lower negative values => greater North render. Higher positive values => greater South render.

            // ! DEBUG
            float debugDisplacement = 8.0F; // % Adding to the displacement adds towards the EAST
            int displacementInQuadrants = (int)Math.floor(debugDisplacement/8.0F);
            // ! -----

            // > A texture displacement in pixels should be added in order to not render the same part of the clouds texture on consecutive layers.
            // < Pixel displacement should be done in multiples of 8 (explained below).
            // < It is best to displace the texture in a single direction; the easiest way is towards the EAST.
            // < This is because the displacement value would be added to the drawing position of each vertex.
            // < and for each 8 pixels displace 1 quadrant would be subtracted from the westToEast variable, which determines the square region of the texture to draw (or pick pixels from).
            for(westToEastSpan = -3 - displacementInQuadrants; westToEastSpan <= 4 - displacementInQuadrants; ++westToEastSpan) {
                for(northToSouthSpan = -3; northToSouthSpan <= 4; ++northToSouthSpan) {

                    // - The value of these variables determine where to draw clouds in the previously set render distance.
                    // / Lower values make clouds render twice in the same place.
                    // / While higher values put a gap between drawing quadrants.
                    float westToEastDrawPos = (float)(westToEastSpan * 8);
                    float northToSouthDrawPos = (float)(northToSouthSpan * 8);

                    // + Here vertex are drawn into the builder.
                    // * The float values added to the drawing positions of the vertex determine the pixel of the clouds.png texture positions to be drawn.
                    // * Clouds are drawn in square groups of 8x8 pixels that I called "quadrants" (they are cloud-chunks in a way) [(0,8);(8,8);(8,0);(0,0)].
                    // * Higher or lower values make clouds split or overlap. The size of quadrants are determined by the previous variables.
                    // > If we want to add a displacement to the cloud layer; we first need for the loop variables to be located in that part of the texture before we try to draw said part of the texture.
                    // < Since here we are drawing quadrants, 1 unit up there in the loop is equal to 8 pixels.
                    // < For a displacement of 100 pixels, we would need 100/8 quadrants; that's why displacement should be set in multiples of 8.
                    // _ So, in order to displace by 1 (quadrant) the cloud layer, horizontal displacement should be = 8 and westToEastSpan should be -= 1 for both of its ends.

                    // ? This renders the bottom face of clouds.
                    if (ab > -5.0F) {
                        // The added values to each vertex should be modified all at once and by the same amount; changing a single value makes it draw in the specified way only when looking at the direction of that vertex.
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }

                    // ? This renders the top face of clouds.
                    if (ab <= 5.0F) {
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + debugDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F + debugDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }

                    // ? This renders the west face of clouds.
                    // * This is the only culling if sentence where we take displacement into account.
                    // * Since we are displacing all layers to the opposite direction (East) by default.
                    int ag;
                    if (westToEastSpan > -1 - displacementInQuadrants) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + debugDisplacement), (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + debugDisplacement), (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + debugDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    // ? This renders the east face of clouds.
                    if (westToEastSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    // ? This renders the north face of clouds.
                    if (northToSouthSpan > -1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }

                    // ? This renders the south face of clouds.
                    if (northToSouthSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + debugDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + debugDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
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