package com.github.Soulphur0.config.updaters;

import com.github.Soulphur0.config.command.EanCommandHelp;
import com.github.Soulphur0.config.options.ChunkUnloadingConditions;
import com.github.Soulphur0.config.singletons.WorldRenderingConfig;
import com.github.Soulphur0.networking.server.EanServerPacketSender;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WorldRenderingConfigUpdater {

    public static void setUseEanChunkUnloading(String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // _ Print help if the value was -help.
        if (value.equals("-help")){
            context.getSource().sendMessage(EanCommandHelp.setUseEanChunkUnloading());
            return;
        }

        // _ Validate value.
        if(!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))){
            context.getSource().sendMessage(Text.translatable("command.error.value.boolean").formatted(Formatting.RED));
            return;
        }

        try{
            boolean useEanChunkUnloading = Boolean.parseBoolean(value);

            // Write new value to current config instance.
            WorldRenderingConfig.getOrCreateInstance().setUseEanChunkUnloading(useEanChunkUnloading);

            // Write new value to the server's config file.
            WorldRenderingConfig.writeToDisk();

            // Sync settings with all connected clients.
            EanServerPacketSender.syncAllClientsConfigWithServer(context.getSource().getServer());

            // Notify command's source of the changes.
            String message = (useEanChunkUnloading) ? "Chunk unloading is now enabled." : "Chunk unloading is now disabled.";
            context.getSource().sendMessage(Text.of(message));
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    public static void setChunkUnloadingCondition(String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // _ Print help if the value was -help.
        if (value.equals("-help")){
            context.getSource().sendMessage(EanCommandHelp.setChunkUnloadingCondition());
            return;
        }

        try{
            ChunkUnloadingConditions chunkUnloadingCondition = ChunkUnloadingConditions.valueOf(value.toUpperCase());

            // Write new value to current config instance.
            WorldRenderingConfig.getOrCreateInstance().setChunkUnloadingCondition(chunkUnloadingCondition);

            // Write new value to the server's config file.
            WorldRenderingConfig.writeToDisk();

            // Sync settings with all connected clients.
            EanServerPacketSender.syncAllClientsConfigWithServer(context.getSource().getServer());

            // Notify command's source of the changes.
            String message = "Set chunk unloading condition to " + chunkUnloadingCondition;
            context.getSource().sendMessage(Text.of(message));
        } catch (Exception e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    public static void setChunkUnloadingSpeed(String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        // _ Print help if the value was -help.
        if (value.equals("-help")){
            context.getSource().sendMessage(EanCommandHelp.setChunkUnloadingSpeed());
            return;
        }

        try{
            double chunkUnloadingSpeed = Double.parseDouble(value);

            // Write new value to current config instance.
            WorldRenderingConfig.getOrCreateInstance().setUnloadingSpeed(chunkUnloadingSpeed);

            // Write new value to disk, notifying settings change to the server.
            WorldRenderingConfig.writeToDisk();

            // Sync settings with all connected clients.
            EanServerPacketSender.syncAllClientsConfigWithServer(context.getSource().getServer());

            // Notify command's source of the changes.
            String message = "The speed at which chunks will stop loading/generating when flying with elytra is now " + value + "m/s.";
            context.getSource().sendMessage(Text.of(message));
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }

    public static void setChunkUnloadingHeight(String value, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // _ Print help if the value was -help.
        if (value.equals("-help")){
            context.getSource().sendMessage(EanCommandHelp.setChunkUnloadingHeight());
            return;
        }

        try{
            double chunkUnloadingHeight = Double.parseDouble(value);

            // Write new value to current config instance.
            WorldRenderingConfig.getOrCreateInstance().setUnloadingHeight(chunkUnloadingHeight);

            // Write new value to disk, notifying settings change to the server.
            WorldRenderingConfig.writeToDisk();

            // Sync settings with all connected clients.
            EanServerPacketSender.syncAllClientsConfigWithServer(context.getSource().getServer());

            // Notify command's source of the changes.
            String message = "The height at which chunks will stop loading/generating when flying with elytra is now " + value + "m of altitude.";
            context.getSource().sendMessage(Text.of(message));
        } catch (NumberFormatException e){
            throw new SimpleCommandExceptionType(Text.translatable("command.error.value")).create();
        }
    }
}
