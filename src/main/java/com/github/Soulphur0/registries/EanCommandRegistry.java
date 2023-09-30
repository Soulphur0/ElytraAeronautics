package com.github.Soulphur0.registries;

import com.github.Soulphur0.config.command.EanCommand;

public class EanCommandRegistry {

    public static void registerEanCommands(){
        EanCommand.buildAndRegister();
    }
}
