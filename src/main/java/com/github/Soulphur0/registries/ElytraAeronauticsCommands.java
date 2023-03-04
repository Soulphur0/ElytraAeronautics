package com.github.Soulphur0.registries;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ElytraAeronauticsCommands {

    public static void register(){

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("ean")
            // $ Cloud layer config command
            .then(literal("configCloudLayer")
                .then(argument("layerNumber", integer())
                // + Layer attribute name argument
                    .then(argument("layerAttribute", string())
                                    .suggests()
                        // * Layer attribute value argument
                        .then(argument("value", string())
                            .executes(context -> {
                                int layerNumber = IntegerArgumentType.getInteger(context, "layerNumber");
                                String layerAttribute = StringArgumentType.getString(context, "layerAttribute");
                                String attributeValue = StringArgumentType.getString(context, "value");

                                context.getSource().sendFeedback(Text.literal("This is a test for attribute " + layerAttribute + "! Layer number was: " + layerNumber + " value was " + attributeValue));
                                return 1;
                            })
                        )
                    )
                )
            )
        ));
    }
}