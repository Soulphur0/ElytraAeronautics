package com.github.Soulphur0.config.command;

import com.github.Soulphur0.config.EanClientSettings;
import com.github.Soulphur0.config.updaters.FlightConfigUpdater;
import com.github.Soulphur0.config.updaters.WorldRenderingConfigUpdater;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Set;

public class EanCommandExecutor {

    private static final Set<String> highPermissionSettings = Set.of("FlightConfig", "WorldRenderingConfig");

    // ? Run command for the second execution point.
    // ¿ This point configures flight settings, general cloud settings & world rendering settings.
    public static void executionPoint2(String configMode, String option, String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        // + Validate high permission commands.
        if (highPermissionSettings.contains(configMode) && !context.getSource().hasPermissionLevel(4)){
            context.getSource().sendMessage(Text.literal("You require to be an operator in order to change server-side settings.").formatted(Formatting.RED));
            return;
        }

        // + Update a setting and/or sync it with clients.
        switch (configMode) {
            case "FlightConfig" -> {
                switch (option) {
                    case "altitudeDeterminesSpeed" -> FlightConfigUpdater.setAltitudeDeterminesSpeed(value, context);
                    case "minSpeed" -> FlightConfigUpdater.setMinSpeed(value, context);
                    case "maxSpeed" -> FlightConfigUpdater.setMaxSpeed(value, context);
                    case "minHeight" -> FlightConfigUpdater.setMinHeight(value, context);
                    case "maxHeight" -> FlightConfigUpdater.setMaxHeight(value, context);
                    case "sneakingRealignsPitch" -> FlightConfigUpdater.setSneakingRealignsPitch(value, context);
                    case "realignAngle" -> FlightConfigUpdater.setRealignAngle(value, context);
                    case "realignRate" -> FlightConfigUpdater.setRealignRate(value, context);
                    default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                }
            }
            case "CloudConfig" -> {
                switch (option) {
                    case "useEanCloudRendering" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("generalCloudConfig","useEanCloudRendering", value), context);
                    case "setCloudLayerAmount" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("generalCloudConfig","setCloudLayerAmount", value), context);
                    case "loadPreset" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("generalCloudConfig","loadPreset", value), context);
                    default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                }
            }
            case "WorldRenderingConfig" -> {
                switch (option) {
                    case "useEanChunkUnloading" -> WorldRenderingConfigUpdater.setUseEanChunkUnloading(value, context);
                    case "setChunkUnloadingCondition" -> WorldRenderingConfigUpdater.setChunkUnloadingCondition(value, context);
                    case "setChunkUnloadingSpeed" -> WorldRenderingConfigUpdater.setChunkUnloadingSpeed(value, context);
                    case "setChunkUnloadingHeight" -> WorldRenderingConfigUpdater.setChunkUnloadingHeight(value, context);
                    default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
                }
            }
        }
    }

    // ? Run command for the third execution point.
    // ¿ This point configures cloudLayerConfig settings.
    public static void executionPoint3(String configMode, String option1, String option2, String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        // + This is the execution point for in the CloudConfig context.
        // * option1 == cloudLayerNumber
        // * option2 == cloudLayerAttribute
        if (configMode.equals("CloudConfig")){
            switch (option2) {
                case "altitude" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","altitude", option1, value), context);
                case "cloudType" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","cloudType", option1, value), context);
                case "verticalRenderDistance" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","verticalRenderDistance", option1, value), context);
                case "horizontalRenderDistance" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","horizontalRenderDistance", option1, value), context);
                case "lodRenderDistance" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","lodRenderDistance", option1, value), context);
                case "thickness" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","thickness", option1, value), context);
                case "color" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","color", option1, value), context);
                case "opacity" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","opacity", option1, value), context);
                case "shading" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","shading", option1, value), context);
                case "speed" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","speed", option1, value), context);
                case "skyEffects" -> EanServerPacketSender.sendUpdatedClientConfig(new EanClientSettings("cloudLayerConfig","skyEffects", option1, value), context);
                default -> throw new SimpleCommandExceptionType(Text.translatable("command.error.attribute")).create();
            }
        }
    }
}
