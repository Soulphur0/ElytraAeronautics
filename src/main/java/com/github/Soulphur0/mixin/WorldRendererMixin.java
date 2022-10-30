package com.github.Soulphur0.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {
    /*
    // * Constants
    float infinity = Float.MAX_VALUE;

    // * Capture variables from the original method.
    BufferBuilder renderCloudsBufferBuilder;
    double renderCloudsX;
    double renderCloudsY;
    double renderCloudsZ;
    Vec3d color;

    @ModifyArgs(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;"))
    private void getRenderCloudsInfo(Args args) {
        renderCloudsBufferBuilder = args.get(0);
        renderCloudsX = args.get(1);
        renderCloudsY = args.get(2);
        renderCloudsZ = args.get(3);
        color = args.get(4);
    }

    // * Config file data
    EanConfigFile configFile;
    List<CloudLayer> cloudLayers = new ArrayList<>();

    // * Method which calls the render method for each layer there is.
    @Inject(method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "TAIL", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V"))
    private void additionalRenders(BufferBuilder builder, double x, double y, double z, Vec3d color, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir){
        // ? Get cloud layers from config file (once)
        if (ElytraAeronautics.readConfigFileCue_WorldRendererMixin){
            configFile = ConfigFileReader.getConfigFile();
            cloudLayers = configFile.getCloudLayerList();
            ElytraAeronautics.readConfigFileCue_WorldRendererMixin = false;
        }

        // ? Individual layer variables
        float horizontalDisplacement; // horizontalDisplacement --> How displaced are clouds horizontally from the default cloud layer, (this avoids both layers to have the same cloud pattern)
        float verticalDisplacement; // verticalDisplacement --> Added altitude from the default cloud height

        CloudTypes cloudType;
        CloudRenderModes renderMode;
        CloudRenderModes lodRenderMode;

        float previousPreviousAltitude = 0;
        float previousAltitude = 0;
        float distanceFromPreviousPreviousLayer;
        float distanceFromLastLayer;

        // ? Get values and render each individual layer
        int counter = 0;
        for(CloudLayer layer : cloudLayers){
            // Render info
            horizontalDisplacement = (counter+1)*100;
            verticalDisplacement = layer.getAltitude()-192.0F;
            cloudType = layer.getCloudType();

            // Render mode info
            renderMode = layer.getRenderMode();
            float renderDistance = layer.getCloudRenderDistance();
            boolean usingCustomRenderDistance = false;

            lodRenderMode = layer.getLodRenderMode();
            float lodRenderDistance = layer.getLodRenderDistance();
            boolean usingCustomLODRenderDistance = false;
            boolean usingSmoothLODs = layer.isUseSmoothLODs();

            // Calculate relative distances
            if (counter == 0)
                distanceFromPreviousPreviousLayer = infinity;
            else if (counter == 1)
                distanceFromPreviousPreviousLayer = layer.getAltitude() - 192.0F;
            else
            distanceFromPreviousPreviousLayer = layer.getAltitude() - previousPreviousAltitude;
            distanceFromLastLayer = (counter > 0) ? layer.getAltitude() - previousAltitude : layer.getAltitude() - 192.0F;

            previousPreviousAltitude = previousAltitude;
            previousAltitude = layer.getAltitude();

            // Set which relative distances to use
            switch (renderMode){
                case NEVER_RENDER -> {}
                case TWO_IN_ADVANCE -> renderDistance = distanceFromPreviousPreviousLayer;
                case ONE_IN_ADVANCE -> renderDistance = distanceFromLastLayer;
                case CUSTOM_ALTITUDE -> usingCustomRenderDistance = true;
                case ALWAYS_RENDER -> renderDistance = infinity;
            }

            switch (lodRenderMode){
                case TWO_IN_ADVANCE -> lodRenderDistance = distanceFromPreviousPreviousLayer;
                case ONE_IN_ADVANCE -> lodRenderDistance = distanceFromLastLayer;
                case CUSTOM_ALTITUDE -> usingCustomLODRenderDistance = true;
                case ALWAYS_RENDER -> lodRenderDistance = infinity;
            }

            // Call render method
            if (!CloudRenderModes.NEVER_RENDER.equals(renderMode))
                renderCloudLayer(horizontalDisplacement, verticalDisplacement, cloudType, renderDistance, lodRenderDistance, usingSmoothLODs, usingCustomRenderDistance, usingCustomLODRenderDistance, layer.getAltitude());
            counter++;
        }
    }

    // FIXME There are two current issues regarding this method:
    //  The bottom face of clouds doesn't render when looking at it from the lower half of the cloud inside the cloud.
    //  When using smooth LODs, clouds sometimes pop-in at full size before returning flat and starting to puff up.
    private void renderCloudLayer(float horizontalDisplacement, float verticalDisplacement, CloudTypes cloudType, float renderDistance, float highLODDistance, boolean usingSmoothLODs,
                                  boolean customRenderDistance, boolean customLODRenderDistance, float cloudAltitude) // Parameters used only when customRenderAltitude is being used.
    {
        // ? CONSTANTS
        float k = (float)MathHelper.floor(renderCloudsX) * 0.00390625F;
        float l = (float)MathHelper.floor(renderCloudsZ) * 0.00390625F;
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

        // ? Variables
        float playerRelativeDistanceFromCloudLayer = (float)Math.floor(renderCloudsY / 4.0D) * 4.0F;
        playerRelativeDistanceFromCloudLayer += verticalDisplacement;
        float cloudThickness = 4.0F;

        if (usingSmoothLODs) {
            float puffUpStartDistance = (highLODDistance+4.0F)/2;
            float puffUpStopDistance = (highLODDistance+4.0F)/5;
            if (CloudTypes.LOD.equals(cloudType) && playerRelativeDistanceFromCloudLayer > puffUpStartDistance){
                cloudThickness = 0.0F;
            } else if (CloudTypes.LOD.equals(cloudType) && playerRelativeDistanceFromCloudLayer < puffUpStartDistance && playerRelativeDistanceFromCloudLayer > puffUpStopDistance){
                cloudThickness = EanMath.getLinealValue(puffUpStartDistance,0,puffUpStopDistance,4,playerRelativeDistanceFromCloudLayer);
            }
        }

        // ? Render predicates
        boolean fancyClouds = cloudType.equals(CloudTypes.FANCY);
        boolean fastClouds = cloudType.equals(CloudTypes.FAST);
        boolean lodClouds = cloudType.equals(CloudTypes.LOD);

        // Check if the player is withing the render distance either if custom render altitude is being used or not.
        boolean withinRenderDistance;
        boolean withinHighLODDistance;
        if (customRenderDistance){
            withinRenderDistance = playerRelativeDistanceFromCloudLayer < (cloudAltitude-renderDistance);
        } else {
            withinRenderDistance = playerRelativeDistanceFromCloudLayer < renderDistance;
        }

        if (customLODRenderDistance){
            withinHighLODDistance = playerRelativeDistanceFromCloudLayer < (cloudAltitude-highLODDistance);
        } else {
            withinHighLODDistance = playerRelativeDistanceFromCloudLayer < highLODDistance;
        }


        // * RENDER FANCY clouds either if (fancy clouds are enabled and withing render range) or (within high LOD altitude range and maximum LOD render distance).
        if (fancyClouds && withinRenderDistance || lodClouds && withinHighLODDistance){
            for(int ac = MathHelper.floor(-0.125*horizontalDisplacement-3); ac <= MathHelper.floor(-0.125*horizontalDisplacement+4); ++ac) {
                for(int ad = -3; ad <= 4; ++ad) {
                    float ae = (float)(ac * 8);
                    float af = (float)(ad * 8);

                    // This renders the bottom face of clouds.
                    if (playerRelativeDistanceFromCloudLayer > -6.0F) {
                        renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }

                    // This renders the top face of clouds.
                    if (playerRelativeDistanceFromCloudLayer <= 5.0F) {
                        renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness - 9.765625E-4F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }

                    int aj;
                    // This renders the left face of clouds.
                    // Horizontal displacement is added to the if statement to properly cull the west face of clouds.
                    if (ac > -1 - horizontalDisplacement) {
                        for(aj = 0; aj < 8; ++aj) {
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }

                    if (ac <= 1) {
                        // This renders the right face of clouds.
                        for(aj = 0; aj < 8; ++aj) {
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 8.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + (float)aj + 1.0F - 9.765625E-4F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + (float)aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }
                    // This renders the front(north) face of clouds.
                    if (ad > -1) {
                        for(aj = 0; aj < 8; ++aj) {
                            renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }
                    // This renders the back(south) face of clouds.
                    if (ad <= 1) {
                        for(aj = 0; aj < 8; ++aj) {
                            renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + cloudThickness, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + (float)aj + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)aj + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                        }
                    }
                }
            }
        }
        // * RENDER FAST clouds either if (fast clouds are enabled and withing render range) or (within maximum LOD render distance).
        else if (fastClouds && withinRenderDistance || lodClouds && withinRenderDistance) {
            for(int ac = MathHelper.floor(-0.125*horizontalDisplacement-3); ac <= MathHelper.floor(-0.125*horizontalDisplacement+4); ++ac) {
                for(int ad = -3; ad <= 4; ++ad) {
                    float ae = (float) (ac * 8);
                    float af = (float) (ad * 8);
                    renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    renderCloudsBufferBuilder.vertex(ae + 8.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    renderCloudsBufferBuilder.vertex(ae + 0.0F + horizontalDisplacement, playerRelativeDistanceFromCloudLayer + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }
    }*/
}