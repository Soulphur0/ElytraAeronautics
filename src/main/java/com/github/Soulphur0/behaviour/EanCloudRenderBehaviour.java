
package com.github.Soulphur0.behaviour;

import com.github.Soulphur0.config.EanConfig;
import com.github.Soulphur0.config.cloudlayer.CloudLayer;
import com.github.Soulphur0.config.cloudlayer.CloudTypes;
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
    public static boolean configUpdated = false;

    private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");

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

            double k = (double) (((float) worldRenderer.getTicks() + tickDelta) * 0.03F); // = This determines the speed of clouds; default => 0.03F

            double l = (camPosX + k) / 12.0D;  // % Position x?
            double n = camPosZ / 12.0D + 0.33000001311302185D; // % Position z?

            l -= (double) (MathHelper.floor(l / 2048.0D) * 2048); // % Distance to move in x?
            n -= (double) (MathHelper.floor(n / 2048.0D) * 2048); // % Distance to move in z?

            float o = (float) (l - (double) MathHelper.floor(l)); // % Distance to translate in x
            float q = (float) (n - (double) MathHelper.floor(n)); // % Distance to translate in z

            // + Clear WorldRenderer's cloud buffer.
            // FIXME clouds won't be marked as dirty if the player stays still.
            //  I have to find a way to clear the buffer otherwise the memory leak present
            //  eventually floods the memory if the player stays still.
            if (worldRenderer.getCloudsDirty()) {
                worldRenderer.setCloudsDirty(false);
                if (worldRenderer.getCloudsBuffer() != null) {
                    worldRenderer.getCloudsBuffer().close();
                }
            }

            // = Load config & create layer array stored in the CloudLayer class.
            EanConfig config = AutoConfig.getConfigHolder(EanConfig.class).getConfig();
            CloudLayer.cloudLayers = new CloudLayer[config.numberOfLayers];

            // + Build geometry for each cloud layer.
            // * The geometry for each layer is built using its own parameters, and is stored in an array.
            // * All values besides the geometry that are important for rendering are stored in an array too.
            for (int layerNum = 0; layerNum < config.numberOfLayers; layerNum++) {
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                Vec3d vec3d = worldRenderer.getWorld().getCloudsColor(tickDelta);

                // - Get cloud relative Y distance to the camera.
                double cloudRenderAltitude = (double) ((config.firstLayerAltitude + config.distanceBetweenLayers * layerNum) - (float) camPosY + 0.33F);

                // TODO branch 05/03/23
                // - Create and set values for the layer.
                // = Parameters that the player will be able to tweak in the config.
                CloudLayer layer = new CloudLayer();
                layer.setName("Layer " + layerNum);
                layer.setAltitude((config.firstLayerAltitude + config.distanceBetweenLayers * layerNum));
                layer.setCloudType(config.cloudType);
                layer.setVerticalRenderDistance(config.verticalRenderDistance);
                layer.setHorizontalRenderDistance(config.horizontalRenderDistance);
                layer.setLodRenderDistance(config.lodRenderDistance);
                layer.setUseSmoothLODs(config.useSmoothLods);
                layer.setCloudThickness(config.cloudThickness);

                // = Rendering context parameters.
                // ; The layer's texture displacement towards the right.
                layer.setDisplacement(layerNum * 64);

                // ; Exact render altitude of the layer and store it in the altitudes array.
                layer.setRenderAltitude((float) (cloudRenderAltitude / layer.getCloudThickness() - (double) MathHelper.floor(cloudRenderAltitude / layer.getCloudThickness())) * layer.getCloudThickness());

                // ; Whether the layer is within a certain render distance.
                layer.setWithinRenderDistance(Math.abs(layer.getAltitude() - camPosY) <= config.verticalRenderDistance);
                layer.setWithinLodRenderDistance(Math.abs(layer.getAltitude() - camPosY) <= config.verticalRenderDistance);

                // ; The geometry of the cloud layer.
                worldRenderer.setCloudsBuffer(new VertexBuffer());
                layer.setVertexGeometry(ean_renderCloudLayers(layer, bufferBuilder, l, cloudRenderAltitude, n, vec3d)); // > Cloud rendering entry.

                // = Store later object in the layers array to later render.
                CloudLayer.cloudLayers[layerNum] = layer;

                // TODO Find out what these parameters do...
                int r = (int) Math.floor(l);
                int s = (int) Math.floor(cloudRenderAltitude / config.cloudThickness);
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
            // * Using the previously generated array, clouds are rendered with their own settings.
            for (CloudLayer layer : CloudLayer.cloudLayers) {

                // > Added this conditional clause.
                // < Since geometries are set to null after they are drawn a couple of times.
                // < Most likely because they are cleared off by the garbage collector, but I don't know that much about OpenGL ATM.
                if (layer.getVertexGeometry() != null) {
                    worldRenderer.getCloudsBuffer().bind();
                    worldRenderer.getCloudsBuffer().upload(layer.getVertexGeometry());
                    VertexBuffer.unbind();

                    // * Get shader, texture and background to draw with cloud geometry.
                    RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
                    RenderSystem.setShaderTexture(0, CLOUDS);
                    BackgroundRenderer.setFogBlack();

                    // * Scale cloud geometry to cloud size and translate it.
                    matrices.push();
                    matrices.scale(12.0F, 1.0F, 12.0F);
                    matrices.translate(-o, layer.getRenderAltitude(), -q);

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

                }
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    private static BufferBuilder.BuiltBuffer ean_renderCloudLayers(CloudLayer layer, BufferBuilder bufferBuilder, double camX, double camY, double camZ, Vec3d color) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        // + Puff-up clouds.
        if (layer.getCloudType().equals(CloudTypes.LOD)){
            // * Sets puff-up start altitude.
            float puffUpStartAltitude = layer.getLodRenderDistance();

            // * Puff-up clouds gradually or suddenly.
            if (layer.isUseSmoothLODs())
                layer.setCloudThickness(Math.min((float) EanMath.getLinealValue(puffUpStartAltitude, 0, layer.getAltitude(), 4, camY), 4.0F));
            else
                layer.setCloudThickness((layer.getLodRenderDistance() - camY <= 0) ?  4.0F : 0.0F);
        }

        // + Draw cloud layer into the buffer.
        ean_renderCloudLayer(layer, bufferBuilder, camX, camY, camZ, color);

        // + Return built buffer
        return bufferBuilder.end();
    }

    private static void ean_renderCloudLayer(CloudLayer layer, BufferBuilder builder, double x, double y, double z, Vec3d color){
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

        float cloudThickness = layer.getCloudThickness();
        float ab = (float)Math.floor(y / cloudThickness) * cloudThickness;

        if (layer.getCloudType().equals(CloudTypes.FANCY) && layer.isWithinRenderDistance()) {
            // + The following 'for' loop counters determine cloud render distance.
            // * Much like the Minecraft world, cloud are rendered in chunks that I will call 'quadrants'.
            // * Cloud quadrants are made out 8x8 flat blocks. One quadrant would equal 1 single iteration of this loop.
            // * A flat block is in reality a single pixel of the clouds texture inflated into a cloud.

            // - The value of these counters determine the span in quadrants of cloud rendering in cardinal directions.
            // / They also determine (in quadrants) the region of the clouds texture to render.
            int westToEastSpan; // Lower negative values => greater West render. Higher positive values => greater East render.
            int northToSouthSpan; // Lower negative values => greater North render. Higher positive values => greater South render.

            // > A texture displacement in pixels has to be added in order to not render the same part of the clouds texture on consecutive layers.
            // < It is best to displace the texture in a single direction; I arbitrarily chose the EAST.
            float textureDisplacement = layer.getDisplacement(); // Adding to the displacement adds towards the EAST
            int displacementInQuadrants = (int)Math.floor(textureDisplacement/8.0F);

            // > Pixel displacement should be done in multiples of 8.
            // < The displacement value is added to the drawing position of each vertex, for every 8 pixels of displace,
            // < 1 quadrant is subtracted from the westToEast variable, which determines the square region of the texture to draw.
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
                    if (ab > -8.0F) {
                        // The added values to each vertex should be modified all at once and by the same amount; changing a single value makes it draw in the specified way only when looking at the direction of that vertex.
                        builder.vertex((double)(westToEastDrawPos + 0.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }

                    // ? This renders the top face of clouds.
                    if (ab <= 5.0F) {
                        builder.vertex((double)(westToEastDrawPos + 0.0F + textureDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + textureDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 8.0F + textureDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex((double)(westToEastDrawPos + 0.0F + textureDisplacement), (double)(ab + cloudThickness - 9.765625E-4F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }

                    // ? This renders the west face of clouds.
                    // * This is the only culling if sentence where we take displacement into account.
                    // * Since we are displacing all layers to the opposite direction (East) by default.
                    int ag;
                    if (westToEastSpan > -1 - displacementInQuadrants) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + textureDisplacement), (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + textureDisplacement), (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 0.0F + textureDisplacement), (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    // ? This renders the east face of clouds.
                    if (westToEastSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 8.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex((double)(westToEastDrawPos + (float)ag + 1.0F - 9.765625E-4F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + 0.0F)).texture((westToEastDrawPos + (float)ag + 0.5F) * 0.00390625F + k, (northToSouthDrawPos + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    // ? This renders the north face of clouds.
                    if (northToSouthSpan > -1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 0.0F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }

                    // ? This renders the south face of clouds.
                    if (northToSouthSpan <= 1) {
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + textureDisplacement, (double)(ab + cloudThickness), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 8.0F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 8.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex((double)(westToEastDrawPos + 0.0F) + textureDisplacement, (double)(ab + 0.0F), (double)(northToSouthDrawPos + (float)ag + 1.0F - 9.765625E-4F)).texture((westToEastDrawPos + 0.0F) * 0.00390625F + k, (northToSouthDrawPos + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                        }
                    }
                }
            }
        } else if (false) {
            for(int ah = -32; ah < 32; ah += 32) {
                for(int ai = -32; ai < 32; ai += 32) {
                    builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 32)).texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 32)).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 0)).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 0)).texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }
    }
}