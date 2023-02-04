package com.github.Soulphur0.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessors {

    @Accessor
    ClientWorld getWorld();

    @Accessor
    int getTicks();

    @Accessor
    int getLastCloudsBlockX();

    @Accessor
    int getLastCloudsBlockY();

    @Accessor
    int getLastCloudsBlockZ();

    @Accessor
    MinecraftClient getClient();

    @Accessor
    CloudRenderMode getLastCloudRenderMode();

    @Accessor
    Vec3d getLastCloudsColor();

    @Accessor
    boolean getCloudsDirty();

    @Accessor
    VertexBuffer getCloudsBuffer();

    @Accessor("lastCloudsBlockX")
    void setLastCloudsBlockX(int lastCloudsBlockX);

    @Accessor("lastCloudsBlockY")
    void setLastCloudsBlockY(int lastCloudsBlockY);

    @Accessor("lastCloudsBlockZ")
    void setLastCloudsBlockZ(int lastCloudsBlockZ);

    @Accessor("lastCloudsColor")
    void setLastCloudsColor(Vec3d lastCloudsColor);

    @Accessor("lastCloudRenderMode")
    void setLastCloudRenderMode(CloudRenderMode lastCloudRenderMode);

    @Accessor("cloudsDirty")
    void setCloudsDirty(boolean cloudsDirty);

    @Accessor("cloudsBuffer")
    void setCloudsBuffer(VertexBuffer cloudsBuffer);
}
