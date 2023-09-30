package com.github.Soulphur0.config.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** Register and building point for the '/ean' command.<br><br>
 *  Suggestions and execution paths are handled on different classes to keep the already lengthy Brigadier functional interface chain as tidy as possible.<br><br>
 */
public class EanCommand {

    public static void buildAndRegister(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("ean")
            // > Execution point 1
            // < Print help if only '/ean' was run.
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayer();
                Text message = Text.literal("\n")
                        .append(Text.literal("Type '/ean' and press TAB to see available options.\nWrite '-help' at the end of each option to see its usage and default values.\n\n").formatted(Formatting.GOLD))
                        .append(Text.literal("You can read an in-depth guide about this command ").append(Text.literal("clicking here.\n").formatted(Formatting.UNDERLINE).styled((style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Soulphur0/Soulphur-Mods-ResoucesAndChangelogs/blob/main/ElytraAeronautics/mod_resources/ean_command_guide.md"))))));

                if (player != null)
                    player.sendMessage(message);

                return 1;
            })
            // $ Argument 1
            // € Choose config category.
            .then(argument("configMode", string())
                .suggests((commandContext, suggestionsBuilder) -> {
                    String[] suggestions = {"FlightConfig", "CloudConfig", "WorldRenderingConfig"};

                    return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                })
                // $ Argument 2
                // € Choose config option/subcommand.
                .then(argument("arg2", string())
                    .suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(EanCommandSuggester.suggestArgument2(commandContext), suggestionsBuilder))
                    // $ Argument 3
                    // € Choose option value/subcommand option.
                    .then(argument("arg3", string())
                        .suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(EanCommandSuggester.suggestArgument3(commandContext), suggestionsBuilder))
                        // > Execution point 2
                        // < Set value for FlightConfig, GeneralCloudConfig or WorldRenderingConfig options.
                        .executes(context -> {
                            String configMode = StringArgumentType.getString(context, "configMode");
                            String option = StringArgumentType.getString(context, "arg2");
                            String value = StringArgumentType.getString(context, "arg3");

                            EanCommandExecutor.executionPoint2(configMode, option, value, context);
                            return 1;
                        })
                        // $ Argument 4
                        // € Choose option/subcommand.
                        .then(argument("arg4", string())
                            .suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(EanCommandSuggester.suggestArgument4(commandContext), suggestionsBuilder))
                            // $ Argument 5
                            // € Choose option/subcommand/value.
                            .then(argument("arg5", string())
                                .suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(EanCommandSuggester.suggestArgument5(commandContext), suggestionsBuilder))
                                    // > Execution point 3
                                    // < Set value for configCloudLayer options.
                                    .executes(context -> {
                                        String configMode = StringArgumentType.getString(context, "configMode");
                                        String option1 = StringArgumentType.getString(context, "arg3");
                                        String option2 = StringArgumentType.getString(context, "arg4");
                                        String value = StringArgumentType.getString(context, "arg5");

                                        EanCommandExecutor.executionPoint3(configMode, option1, option2, value, context);
                                        return 1;
                                    })
                            )
                        ))
                )
            )
        ));
    }
}