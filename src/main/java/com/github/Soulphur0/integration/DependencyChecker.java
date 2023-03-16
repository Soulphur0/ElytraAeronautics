package com.github.Soulphur0.integration;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class DependencyChecker {

    // ? I'm making a method for each dependency, so I have a single place where the dependencies' ID is written; in
    // ? the future I might check for dependencies in a different way once I start using third party functionality more in depth.
    public static boolean checkForClothConfig(){
        ModContainer modContainer = FabricLoader.getInstance().getModContainer("cloth-config").orElse(null);
        return modContainer != null;
    }
}
