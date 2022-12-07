package com.github.Soulphur0.mixin;

import com.github.Soulphur0.ElytraAeronautics;
import com.github.Soulphur0.config.*;
import com.github.Soulphur0.utility.EanMath;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {

    // * Class attributes used by the renderClouds() public method.
    @Shadow
    private ClientWorld world;

    @Shadow
    private int lastCloudsBlockX;

    @Shadow
    private int lastCloudsBlockY;

    @Shadow
    private int lastCloudsBlockZ;

    @Shadow
    private Vec3d lastCloudsColor;

    @Shadow
    @Nullable
    private CloudRenderMode lastCloudRenderMode;

    @Shadow
    private int ticks;

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private boolean cloudsDirty;

    @Final
    @Shadow
    private static Identifier CLOUDS;

    @Shadow
    private VertexBuffer cloudsBuffer;

    @Inject(method= "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At(value = "HEAD"))
    private void renderCloudsPublicOverwrite(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f, CallbackInfo ci){
        float g = this.world.getDimensionEffects().getCloudsHeight();
        if (!Float.isNaN(g)) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            float h = 12.0F;
            float i = 4.0F;
            double j = 2.0E-4D;
            double k = (double)(((float)this.ticks + tickDelta) * 0.03F);
            double l = (d + k) / 12.0D;
            double m = (double)(g - (float)e + 0.33F);
            double n = f / 12.0D + 0.33000001311302185D;
            l -= (double)(MathHelper.floor(l / 2048.0D) * 2048);
            n -= (double)(MathHelper.floor(n / 2048.0D) * 2048);
            float o = (float)(l - (double)MathHelper.floor(l));
            float p = (float)(m / 4.0D - (double)MathHelper.floor(m / 4.0D)) * 4.0F;
            float q = (float)(n - (double)MathHelper.floor(n));
            Vec3d vec3d = this.world.getCloudsColor(tickDelta);
            int r = (int)Math.floor(l);
            int s = (int)Math.floor(m / 4.0D);
            int t = (int)Math.floor(n);
            if (r != this.lastCloudsBlockX || s != this.lastCloudsBlockY || t != this.lastCloudsBlockZ || this.client.options.getCloudRenderModeValue() != this.lastCloudRenderMode || this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4D) {
                this.lastCloudsBlockX = r;
                this.lastCloudsBlockY = s;
                this.lastCloudsBlockZ = t;
                this.lastCloudsColor = vec3d;
                this.lastCloudRenderMode = this.client.options.getCloudRenderModeValue();
                this.cloudsDirty = true;
            }

            if (this.cloudsDirty) {
                this.cloudsDirty = false;
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                if (this.cloudsBuffer != null) {
                    this.cloudsBuffer.close();
                }

                this.cloudsBuffer = new VertexBuffer();
                BufferBuilder.BuiltBuffer builtBuffer = eanCustomCloudRenderSetup(bufferBuilder, l, m, n, vec3d);
                this.cloudsBuffer.bind();
                this.cloudsBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
            RenderSystem.setShaderTexture(0, CLOUDS);
            BackgroundRenderer.setFogBlack();
            matrices.push();
            matrices.scale(12.0F, 1.0F, 12.0F);
            matrices.translate((double)(-o), (double)p, (double)(-q));
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.bind();
                int u = this.lastCloudRenderMode == CloudRenderMode.FANCY ? 0 : 1;

                for(int v = u; v < 2; ++v) {
                    if (v == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }

                    ShaderProgram shaderProgram = RenderSystem.getShader();
                    this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                }

                VertexBuffer.unbind();
            }

            matrices.pop();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    EanConfigFile configFile;
    List<CloudLayer> cloudLayers = new ArrayList<>();
    private BufferBuilder.BuiltBuffer eanCustomCloudRenderSetup(BufferBuilder builder, double x, double renderCloudsY, double z, Vec3d color){
        // ? Get cloud layers from config file (once per save action)
        if (ElytraAeronautics.readConfigFileCue_WorldRendererMixin){
            configFile = ConfigFileReader.getConfigFile();
            cloudLayers = configFile.getCloudLayerList();
            ElytraAeronautics.readConfigFileCue_WorldRendererMixin = false;
        }

        // ? Return buffer with all cloud layers drawn as the config specifies.
        return renderCloudsPrivateOverwrite(cloudLayers, builder, x, renderCloudsY, z, color);
    }

    // FIXME There are two current issues regarding this method:
    //  Cloud configuration is not working as intended.
    //  The bottom face of clouds doesn't render when looking at it from the lower half of the cloud inside the cloud.
    //  When using smooth LODs, clouds sometimes pop-in at full size before returning flat and starting to puff up.
    private BufferBuilder.BuiltBuffer renderCloudsPrivateOverwrite(List<CloudLayer> cloudLayers,BufferBuilder builder, double x, double renderCloudsY, double z, Vec3d color){
        float f = 4.0F;
        float g = 0.00390625F;
        float h = 9.765625E-4F;
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float playerRelativeDistanceFromCloudLayer = (float)Math.floor(renderCloudsY / 4.0D) * 4.0F; // Unmodified original variable.

        // $ ↓↓↓↓↓ BIG CUSTOM CODE BLOCK ↓↓↓↓↓

        // ? Used to displace each layer by 1*constant horizontally, and to calculate relative positions between layers.
        int layerCounter = 0;

        // ? Used to keep track of previous layer's altitudes when placing cloud layers.
        float prePreAltitude = 0;
        float distFromPrePreLayer;

        float preAltitude = 0;
        float distFromPreLayer;

        // ? Used to determine the distance to a non-existing previous layer, or to a layer that always renders.
        float infinity = Float.MAX_VALUE;

        // *** Process each layer ***
        for (CloudLayer layer : cloudLayers){
            // _ Get layer attributes.
            // ? Displacement variables.
            float horizontalDisplacement = (layerCounter + 1)*100; // How displaced are clouds horizontally from the default cloud layer, (this avoids both layers to have the same cloud pattern)
            float verticalDisplacement = layer.getAltitude()-192.0F; // Additional altitude from the default cloud height.

            // ? Cloud render variables.
            CloudTypes cloudType = layer.getCloudType();
            CloudRenderModes renderMode = layer.getRenderMode();
            CloudRenderModes lodRenderMode = layer.getLodRenderMode();
            boolean usingSmoothLODs = layer.isUseSmoothLODs();

            // ? Cloud render distance variables.
            float renderDistance = layer.getCloudRenderDistance();
            boolean usingCustomRenderDistance = renderMode == CloudRenderModes.CUSTOM_ALTITUDE;

            float lodRenderDistance = layer.getLodRenderDistance();
            boolean usingCustomLODRenderDistance = lodRenderMode == CloudRenderModes.CUSTOM_ALTITUDE;

            // _ Calculate render distance.
            // - Calculate distance from two layers behind.
            if (layerCounter == 0)
                distFromPrePreLayer = infinity; // Infinity, the distance of the first layer from its previous one.
            else if (layerCounter == 1)
                distFromPrePreLayer = layer.getAltitude() - 192.0F;
            else
                distFromPrePreLayer = layer.getAltitude() - prePreAltitude;

            // - Calculate distance from the layer behind.
            distFromPreLayer = (layerCounter > 0) ? layer.getAltitude() - preAltitude/2 : layer.getAltitude();

            // - Update previous altitudes, move altitudes one position behind.
            prePreAltitude = preAltitude;
            preAltitude = layer.getAltitude();

            // - Now that numbers are calculated for each mode, set which relative distance mode to use.
            switch (renderMode){
                case TWO_IN_ADVANCE -> renderDistance = distFromPreLayer; // TODO: These are temporarily swapped
                case ONE_IN_ADVANCE -> renderDistance = distFromPrePreLayer; // TODO: These are temporarily swapped
                case ALWAYS_RENDER -> renderDistance = infinity; // Infinity, the render distance to a layer that always renders.
                default -> {}
            }

            switch (lodRenderMode){
                case TWO_IN_ADVANCE -> lodRenderDistance = distFromPrePreLayer;
                case ONE_IN_ADVANCE -> lodRenderDistance = distFromPreLayer;
                case ALWAYS_RENDER -> lodRenderDistance = infinity; // Infinity, the render distance to a layer that always renders.
                default -> {}
            }

            // - Skip render layer if render mode is 'NEVER_RENDER'
            if (CloudRenderModes.NEVER_RENDER.equals(renderMode)) continue;

            // _ Use renderDistance to determine how to render clouds relative to the player.
            // - Check if the player is within the render distance either if custom render altitude is being used or not.
            playerRelativeDistanceFromCloudLayer += verticalDisplacement; // Player distance-to the cloud layer.
            float cloudAltitude = layer.getAltitude(); // Absolute height position of the cloud layer.

            boolean withinRenderDistance = (usingCustomRenderDistance) ? playerRelativeDistanceFromCloudLayer < (cloudAltitude-renderDistance): playerRelativeDistanceFromCloudLayer < renderDistance;
            boolean withinHighLODDistance = (usingCustomLODRenderDistance) ? playerRelativeDistanceFromCloudLayer < (cloudAltitude-lodRenderDistance): playerRelativeDistanceFromCloudLayer < lodRenderDistance;

            // - Puff up clouds.
            float cloudThickness = 4.0F;
            if (usingSmoothLODs) {
                // ? Sets puff-up start and puff-up stop distances.
                float puffUpStartDistance = (lodRenderDistance+4.0F)/2;
                float puffUpStopDistance = (lodRenderDistance+4.0F)/5;

                // ? If the player is too far away, clouds appear as fast clouds, else they linearly puff-up.
                if (CloudTypes.LOD.equals(cloudType) && playerRelativeDistanceFromCloudLayer > puffUpStartDistance){
                    cloudThickness = 0.0F;
                } else if (CloudTypes.LOD.equals(cloudType) && playerRelativeDistanceFromCloudLayer < puffUpStartDistance && playerRelativeDistanceFromCloudLayer > puffUpStopDistance){
                    cloudThickness = EanMath.getLinealValue(puffUpStartDistance,0,puffUpStopDistance,4,playerRelativeDistanceFromCloudLayer);
                }
            }

            // $ ↑↑↑↑↑ BIG CUSTOM CODE BLOCK ↑↑↑↑↑

            // _ Render layers
            // * RENDER FANCY clouds either if (fancy clouds are enabled and withing render range) or (within high LOD altitude range and maximum LOD render distance).
            if (cloudType.equals(CloudTypes.FANCY) && withinRenderDistance || cloudType.equals(CloudTypes.LOD) && withinHighLODDistance){
                for(int ac = MathHelper.floor(-0.125*horizontalDisplacement-3); ac <= MathHelper.floor(-0.125*horizontalDisplacement+4); ++ac) {
                    for(int ad = -3; ad <= 4; ++ad) {
                        float ae = (float)(ac * 8);
                        float af = (float)(ad * 8);

                        // This renders the bottom face of clouds.
                        if (playerRelativeDistanceFromCloudLayer > -6.0F) {
                            builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                            builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                            builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                            builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        }

                        // This renders the top face of clouds.
                        if (playerRelativeDistanceFromCloudLayer <= 5.0F) {
                            builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                            builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                            builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                            builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        }

                        int aj;
                        // This renders the left face of clouds.
                        // Horizontal displacement is added to the if statement to properly cull the west face of clouds.
                        if (ac > -1 - horizontalDisplacement) {
                            for(aj = 0; aj < 8; ++aj) {
                                builder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            }
                        }

                        if (ac <= 1) {
                            // This renders the right face of clouds.
                            for(aj = 0; aj < 8; ++aj) {
                                builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                                builder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            }
                        }
                        // This renders the front(north) face of clouds.
                        if (ad > -1) {
                            for(aj = 0; aj < 8; ++aj) {
                                builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                                builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                                builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                                builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            }
                        }
                        // This renders the back(south) face of clouds.
                        if (ad <= 1) {
                            for(aj = 0; aj < 8; ++aj) {
                                builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                                builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                                builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                                builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            }
                        }
                    }
                }
            }
            // * RENDER FAST clouds either if (fast clouds are enabled and withing render range) or (within maximum LOD render distance).
            else if (cloudType.equals(CloudTypes.FAST) && withinRenderDistance || cloudType.equals(CloudTypes.LOD) && withinRenderDistance) {
                for(int ac = MathHelper.floor(-0.125*horizontalDisplacement-3); ac <= MathHelper.floor(-0.125*horizontalDisplacement+4); ++ac) {
                    for(int ad = -3; ad <= 4; ++ad) {
                        float ae = (float) (ac * 8);
                        float af = (float) (ad * 8);
                        builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }
                }
            }

            layerCounter++;
        }

        return builder.end();
    }
}