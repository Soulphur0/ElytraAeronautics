package com.github.Soulphur0.config.command;

import com.github.Soulphur0.config.options.CloudConfigOptions;
import com.github.Soulphur0.config.options.CloudLayerAttributes;
import com.github.Soulphur0.config.options.CloudPresets;
import com.github.Soulphur0.config.options.WorldRenderingOptions;
import com.github.Soulphur0.config.singletons.CloudConfig;
import com.github.Soulphur0.config.singletons.FlightConfig;

import java.util.ArrayList;
import java.util.Collection;

public class EanCommandSuggester {

    // ? Get suggestions for argument two.
    // ¿ They are either a config option or a subcommand.
    public static Collection<String> suggestArgument2(String configMode){
        Collection<String> suggestions = new ArrayList<>();

        switch (configMode) {
            // + Suggestions for each FlightConfig option.
            case "FlightConfig":
                for (FlightConfig.Options option : FlightConfig.Options.values()) {
                    suggestions.add(option.toString());
                }
                break;
            // + Suggestions for each CloudConfig option.
            case "CloudConfig":
                for (CloudConfigOptions option : CloudConfigOptions.values()) {
                    suggestions.add(option.toString());
                }
                break;
            // + Suggestions for each WorldRenderConfig option.
            case "WorldRenderingConfig":
                for (WorldRenderingOptions option : WorldRenderingOptions.values()) {
                    suggestions.add(option.toString());
                }
                break;
        }

        return suggestions;
    }

    // ? Get suggestions for argument three.
    // ¿ They are either suggestions for subcommands or -help for the option values.
    public static Collection<String> suggestArgument3(String configMode, String optionOrSubcommand){
        Collection<String> suggestions = new ArrayList<>();

        if (configMode.equals("CloudConfig")){
            switch (optionOrSubcommand) {
                // + Suggestions for cloud layer number.
                case "configCloudLayer" -> {
                    suggestions.add("all");
                    try {
                        for (int i = 1; i <= CloudConfig.cloudLayers.length; i++) {
                            suggestions.add(String.valueOf(i));
                        }
                    } catch (NullPointerException e) {
                        suggestions.add("all");
                    }
                }
                // + Suggestions for cloud preset.
                case "loadPreset" -> {
                    suggestions.add("-help");
                    for (CloudPresets preset : CloudPresets.values()) {
                        suggestions.add(preset.toString());
                    }
                }
            }
        } else
            suggestions.add("-help");

        return suggestions;
    }

    // ? Get suggestions for argument four.
    // ¿ They are config options for subcommands.
    public static Collection<String> suggestArgument4(String configMode){
        Collection<String> suggestions = new ArrayList<>();

        if (configMode.equals("CloudConfig")){
            for(CloudLayerAttributes attribute : CloudLayerAttributes.values()){
                suggestions.add(attribute.toString());
            }
        }

        return suggestions;
    }

    // ? Get suggestions for argument five.
    // ¿ They are -help values for subcommand config options.
    public static Collection<String> suggestArgument5(String configMode){
        Collection<String> suggestions = new ArrayList<>();

        if (configMode.equals("CloudConfig")){
            suggestions.add("-help");
        }

        return suggestions;
    }
}
