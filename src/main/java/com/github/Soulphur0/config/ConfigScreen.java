package com.github.Soulphur0.config;

import com.github.Soulphur0.ElytraAeronautics;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigScreen {

    // Get current screen for the builder
    Screen screen = MinecraftClient.getInstance().currentScreen;

    // Screen builder objects
    ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(Text.of("Elytra Aeronautics Configuration Screen"));
    ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    // Config categories
    ConfigCategory elytraFlightSettings = builder.getOrCreateCategory(Text.of("Elytra flight settings"));
    ConfigCategory cloudSettings = builder.getOrCreateCategory(Text.of("Cloud settings"));

    // * Config file
    static public EanConfigFile eanConfigFile;

    public Screen buildScreen(){
        // ? Get config file from config folder
        eanConfigFile = ConfigFileReader.getConfigFile();

        // ? Build elytra flight category
        buildElytraFlightCategory();

        // ? Build cloud configuration category
        buildCloudCategory(eanConfigFile);

        // ? Set what to do when the config screen is saved.
        builder.setSavingRunnable(() ->{
            ConfigFileWriter.writeToFile(eanConfigFile);
            resetToDefault = false;
            ElytraAeronautics.readConfigFileCue_WorldRendererMixin = true;
            ElytraAeronautics.readConfigFileCue_LivingEntityMixin = true;
        });

        // ? Return the screen to show.
        return builder.build();
    }

    // * [Elytra flight] methods and variables
    private void buildElytraFlightCategory(){
        elytraFlightSettings.addEntry(entryBuilder.startBooleanToggle(Text.of("Altitude determines flight speed"), eanConfigFile.isAltitudeDeterminesSpeed())
                .setDefaultValue(true)
                .setTooltip(Text.of("Set to true if you want to elytra flight be faster at higher altitudes. If set to false, your flight speed will be determined by the minimal speed."))
                .setSaveConsumer(eanConfigFile::setAltitudeDeterminesSpeed)
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startDoubleField(Text.of("Minimal flight speed (m/s)"), eanConfigFile.getMinSpeed())
                .setDefaultValue(30.35D)
                .setTooltip(Text.of("Minimal flight speed achieved by travelling at a zero degree angle. (The default value is the vanilla value.)"))
                .setSaveConsumer(newValue -> eanConfigFile.setMinSpeed(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startDoubleField(Text.of("Maximum flight speed (m/s)"), eanConfigFile.getMaxSpeed())
                .setDefaultValue(257.22D)
                .setTooltip(Text.of("Maximum flight speed achieved by travelling at a zero degree angle."))
                .setSaveConsumer(newValue -> eanConfigFile.setMaxSpeed(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startDoubleField(Text.of("Flight speed curve beginning"), eanConfigFile.getCurveStart())
                .setDefaultValue(250.0D)
                .setTooltip(Text.of("Altitude at which flight speed start to increase."))
                .setSaveConsumer(newValue -> eanConfigFile.setCurveStart(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startDoubleField(Text.of("Flight speed curve end"), eanConfigFile.getCurveEnd())
                .setDefaultValue(1000.0D)
                .setTooltip(Text.of("Altitude at which flight speed stops to increase (maximum flight speed is achieved)."))
                .setSaveConsumer(newValue -> eanConfigFile.setCurveEnd(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startBooleanToggle(Text.of("Sneaking realigns pitch."), eanConfigFile.isSneakRealignsPitch())
                .setDefaultValue(true)
                .setTooltip(Text.of("When true, sneaking mid-flight will realign your pitch."))
                .setSaveConsumer(newValue -> eanConfigFile.setSneakRealignsPitch(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startFloatField(Text.of("Pitch realignment angle."), eanConfigFile.getRealignmentAngle())
                .setDefaultValue(0)
                .setTooltip(Text.of("Pitch angle at which the player will align when sneaking mid-flight."))
                .setSaveConsumer(newValue -> eanConfigFile.setRealignmentAngle(newValue))
                .build());

        elytraFlightSettings.addEntry(entryBuilder.startFloatField(Text.of("Pitch realignment rate."), eanConfigFile.getRealignmentRate())
                .setDefaultValue(0.1F)
                .setTooltip(Text.of("Amount of rotation (angle-per-tick), at which the player will be rotating towards the pitch realignment angle when sneaking mid-air."))
                .setSaveConsumer(newValue -> eanConfigFile.setRealignmentRate(newValue))
                .build());
    }

    // * [Cloud category] methods and variables
    boolean resetToDefault = false;

    private void buildCloudCategory(EanConfigFile eanConfigFile){
        // ? Get cloud layer list
        List<CloudLayer> cloudLayerList = eanConfigFile.getCloudLayerList();

        // -- GUI SETUP --
        // ? Variables
        List<AbstractConfigListEntry> layerList = new ArrayList<>(); // Used to store all layer subcategory menus.
        List<List<AbstractConfigListEntry>> layerAttributesList = new ArrayList<>(); // Used to store each layer subcategory menus' attributes.

        // ? General settings
        cloudSettings.addEntry(entryBuilder.startBooleanToggle(Text.of("Reset to the default preset?"), false)
                .setDefaultValue(true).setTooltip(Text.of("Set this to \"yes\" and save to reset cloud layers to the default preset. All previous cloud layers will be removed and 2 new layers will be placed at the flight-speed-curve's middle and highest points."))
                .setSaveConsumer(newValue->{
                    if (newValue){
                        resetToDefault = true;
                        updateLayerListEntries(cloudLayerList,-1);
                    }
                })
                .build());

        cloudSettings.addEntry(entryBuilder.startTextDescription(Text.of("--- General settings ---"))
                .setTooltip(Text.of("Settings applied to all cloud layers at once!"))
                .build());

        // Layer amount field.
        cloudSettings.addEntry(entryBuilder.startIntField(Text.of("Cloud layer amount"), eanConfigFile.getLayerAmount())
                .setTooltip(Text.of("This value determines the amount of cloud layers there are. Besides the vanilla clouds. To make new layers show up in the config, re-enter the menu without restarting. For changes to apply in-game however, restarting is necessary."))
                .setDefaultValue(2)
                .setMin(0)
                .setSaveConsumer(newValue -> {
                    if (newValue != eanConfigFile.getLayerAmount()){
                        // Save layer amount in class field and add all new layers (or remove exceeding ones) from the cloud layer list upon save.
                        eanConfigFile.setLayerAmount(newValue);
                        updateLayerListEntries(cloudLayerList, -1);
                    }
                })
                .build());

        // Distance between layers.
        cloudSettings.addEntry(entryBuilder.startFloatField(Text.of("Distance between cloud layers"), eanConfigFile.getLayerDistance())
                .setTooltip(Text.of("When this value is changed, all cloud layers are relocated, and, starting from the altitude specified in the field below; they are placed on top of each other separated by this distance."))
                .setDefaultValue(250.0F)
                .setMin(0)
                .setSaveConsumer(newValue -> {
                    if (newValue != eanConfigFile.getLayerDistance()){
                        eanConfigFile.setLayerDistance(newValue);
                        updateLayerListEntries(cloudLayerList, 0);
                    }
                })
                .build());

        // Cloud stacking start altitude
        cloudSettings.addEntry(entryBuilder.startFloatField(Text.of("Cloud layers' lowest altitude"), eanConfigFile.getStackingAltitude())
                .setTooltip(Text.of("When the distance between clouds is modified, clouds will re-stacked on top of each other starting at the altitude specified in this field."))
                .setDefaultValue(192.0F)
                .setSaveConsumer(newValue -> {
                    if (newValue != eanConfigFile.getStackingAltitude()){
                        eanConfigFile.setStackingAltitude(newValue);
                        updateLayerListEntries(cloudLayerList, 0);
                    }
                })
                .build());

        // Cloud type
        cloudSettings.addEntry(entryBuilder
                .startDropdownMenu(
                        Text.of("Cloud type"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(eanConfigFile.getCloudType(), value ->
                                switch (value) {
                                    case "FAST" -> CloudTypes.FAST;
                                    case "FANCY" -> CloudTypes.FANCY;
                                    default -> CloudTypes.LOD;
                                }),
                        DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                        {
                            if (CloudTypes.LOD.equals(value)) {
                                return Text.of("LOD");
                            } else if (CloudTypes.FAST.equals(value)) {
                                return Text.of("FAST");
                            } else if (CloudTypes.FANCY.equals(value)) {
                                return Text.of("FANCY");
                            }
                            return Text.of("UNKNOWN CLOUD TYPE");
                        }))
                .setDefaultValue(CloudTypes.LOD)
                .setSuggestionMode(false)
                .setSelections(Arrays.asList(CloudTypes.values()))
                .setSaveConsumer(newValue -> {
                    if (newValue != eanConfigFile.getCloudType()){
                        eanConfigFile.setCloudType(newValue);
                        updateLayerListEntries(cloudLayerList, 1);
                    }
                })
                .build());

        // Render mode
        cloudSettings.addEntry(entryBuilder
                .startDropdownMenu(
                        Text.of("Render mode"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(eanConfigFile.getRenderMode(), value ->
                                switch (value) {
                                    case "NEVER_RENDER" -> CloudRenderModes.NEVER_RENDER;
                                    case "TWO_IN_ADVANCE" -> CloudRenderModes.TWO_IN_ADVANCE;
                                    case "ONE_IN_ADVANCE" -> CloudRenderModes.ONE_IN_ADVANCE;
                                    case "ALWAYS_RENDER" -> CloudRenderModes.ALWAYS_RENDER;
                                    default -> eanConfigFile.getRenderMode();
                                }),
                        DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                        {
                            if (CloudRenderModes.NEVER_RENDER.equals(value)){
                                return Text.of("NEVER_RENDER");
                            } else if (CloudRenderModes.TWO_IN_ADVANCE.equals(value)) {
                                return Text.of("TWO_IN_ADVANCE");
                            } else if (CloudRenderModes.ONE_IN_ADVANCE.equals(value)) {
                                return Text.of("ONE_IN_ADVANCE");
                            } else if (CloudRenderModes.ALWAYS_RENDER.equals(value)) {
                                return Text.of("ALWAYS_RENDER");
                            }
                            return Text.of("");
                        }))
                .setDefaultValue(CloudRenderModes.ALWAYS_RENDER)
                .setTooltip(Text.of("This value determines when a cloud layer begins to render."))
                .setSuggestionMode(false)
                .setSelections(Arrays.asList(CloudRenderModes.values()))
                .setSaveConsumer(newValue ->{
                        if (newValue != eanConfigFile.getRenderMode()){
                            eanConfigFile.setRenderMode(newValue);
                            updateLayerListEntries(cloudLayerList, 2);
                        }
                })
                .build());

        // LOD render mode
        cloudSettings.addEntry(entryBuilder
                .startDropdownMenu(
                        Text.of("LOD render mode"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(eanConfigFile.getLodRenderMode(), value ->
                                switch (value) {
                                    case "TWO_IN_ADVANCE" -> CloudRenderModes.TWO_IN_ADVANCE;
                                    case "ONE_IN_ADVANCE" -> CloudRenderModes.ONE_IN_ADVANCE;
                                    case "ALWAYS_RENDER" -> CloudRenderModes.ALWAYS_RENDER;
                                    default -> eanConfigFile.getLodRenderMode();
                                }),
                        DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                        {
                            if (CloudRenderModes.TWO_IN_ADVANCE.equals(value)) {
                                return Text.of("TWO_IN_ADVANCE");
                            } else if (CloudRenderModes.ONE_IN_ADVANCE.equals(value)) {
                                return Text.of("ONE_IN_ADVANCE");
                            } else if (CloudRenderModes.ALWAYS_RENDER.equals(value)) {
                                return Text.of("ALWAYS_RENDER");
                            }
                            return Text.of("");
                        }))
                .setDefaultValue(CloudRenderModes.TWO_IN_ADVANCE)
                .setTooltip(Text.of("This value determines when a cloud layer begins to render in high level of detail. It is only used if the cloud type is set to LOD."))
                .setSuggestionMode(false)
                .setSelections(Arrays.asList(CloudRenderModes.values()))
                .setSaveConsumer(newValue ->{
                    if (newValue != eanConfigFile.getLodRenderMode()) {
                        eanConfigFile.setLodRenderMode(newValue);
                        updateLayerListEntries(cloudLayerList, 3);
                    }
                })
                .build());

        // Use smooth LODs
        cloudSettings.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Smooth LODs"), eanConfigFile.isUseSmoothLODs())
                .setTooltip(Text.of("Gradually puffs up LOD clouds as the player approaches them so the LOD transition is not too rough. This option may use up more resources. Clouds begin to puff up at half distance from their layer."))
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    if (newValue != eanConfigFile.isUseSmoothLODs()){
                        eanConfigFile.setUseSmoothLODs(newValue);
                        updateLayerListEntries(cloudLayerList,4);
                    }
                })
                .build());

        // ? Layer-specific settings
        // * Put layer attributes in a list, for each layer, to later be displayed in the config screen in order.
        int layerNum = 0;
        for(CloudLayer layer : cloudLayerList){
            // * Make list for layerNum
            layerAttributesList.add(layerNum, new ArrayList<>());

            // * Add layer attributes to layerNum list.
            // Name (Only used for display and file path)
            layer.setName("Layer " + (layerNum+1));

            // Altitude
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startFloatField(Text.of("Altitude"), layer.getAltitude())
                    .setDefaultValue(layer.getAltitude())
                    .setSaveConsumer(layer::setAltitude)
                    .build());

            // Cloud type
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startDropdownMenu(
                            Text.of("Cloud type"),
                            DropdownMenuBuilder.TopCellElementBuilder.of(layer.getCloudType(), value ->
                                    switch (value) {
                                        case "LOD" -> CloudTypes.LOD;
                                        case "FAST" -> CloudTypes.FAST;
                                        case "FANCY" -> CloudTypes.FANCY;
                                        default -> layer.getCloudType();
                                    }),
                            DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                            {
                                if (CloudTypes.LOD.equals(value)) {
                                    return Text.of("LOD");
                                } else if (CloudTypes.FAST.equals(value)) {
                                    return Text.of("FAST");
                                } else if (CloudTypes.FANCY.equals(value)) {
                                    return Text.of("FANCY");
                                }
                                return Text.of("UNKNOWN CLOUD TYPE");
                            }))
                    .setDefaultValue(CloudTypes.LOD)
                    .setSuggestionMode(false)
                    .setSelections(Arrays.asList(CloudTypes.values()))
                    .setSaveConsumer(layer::setCloudType)
                    .build());

            // Render mode
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startDropdownMenu(
                            Text.of("Render mode"),
                            DropdownMenuBuilder.TopCellElementBuilder.of(layer.getRenderMode(), value ->
                                    switch (value) {
                                        case "NEVER_RENDER" -> CloudRenderModes.NEVER_RENDER;
                                        case "TWO_IN_ADVANCE" -> CloudRenderModes.TWO_IN_ADVANCE;
                                        case "ONE_IN_ADVANCE" -> CloudRenderModes.ONE_IN_ADVANCE;
                                        case "CUSTOM_ALTITUDE" -> CloudRenderModes.CUSTOM_ALTITUDE;
                                        case "ALWAYS_RENDER" -> CloudRenderModes.ALWAYS_RENDER;
                                        default -> layer.getRenderMode();
                                    }),
                            DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                            {
                                if (CloudRenderModes.NEVER_RENDER.equals(value)){
                                    return Text.of("NEVER_RENDER");
                                } else if (CloudRenderModes.TWO_IN_ADVANCE.equals(value)) {
                                    return Text.of("TWO_IN_ADVANCE");
                                } else if (CloudRenderModes.ONE_IN_ADVANCE.equals(value)) {
                                    return Text.of("ONE_IN_ADVANCE");
                                } else if (CloudRenderModes.CUSTOM_ALTITUDE.equals(value)) {
                                    return Text.of("CUSTOM_ALTITUDE");
                                } else if (CloudRenderModes.ALWAYS_RENDER.equals(value)) {
                                    return Text.of("ALWAYS_RENDER");
                                }
                                return Text.of("NEVER_RENDER");
                            }))
                    .setDefaultValue(CloudRenderModes.ALWAYS_RENDER)
                    .setTooltip(Text.of("This value determines when the cloud layer begins to render. It can be one/two layers in advance, at a set altitude or always render."))
                    .setSuggestionMode(false)
                    .setSelections(Arrays.asList(CloudRenderModes.values()))
                    .setSaveConsumer(layer::setRenderMode)
                    .build());

            // Render distance
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startFloatField(Text.of("Custom render altitude"), layer.getCloudRenderDistance())
                    .setTooltip(Text.of("This value is only used if \"Custom altitude\" was selected as the render mode."))
                    .setDefaultValue(0.0F)
                    .setSaveConsumer(layer::setCloudRenderDistance)
                    .build());

            // LOD render mode
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startDropdownMenu(
                            Text.of("LOD render mode"),
                            DropdownMenuBuilder.TopCellElementBuilder.of(layer.getLodRenderMode(), value ->
                                    switch (value) {
                                        case "TWO_IN_ADVANCE" -> CloudRenderModes.TWO_IN_ADVANCE;
                                        case "ONE_IN_ADVANCE" -> CloudRenderModes.ONE_IN_ADVANCE;
                                        case "CUSTOM_ALTITUDE" -> CloudRenderModes.CUSTOM_ALTITUDE;
                                        case "ALWAYS_RENDER" -> CloudRenderModes.ALWAYS_RENDER;
                                        default -> layer.getLodRenderMode();
                                    }),
                            DropdownMenuBuilder.CellCreatorBuilder.of(value ->
                            {
                                if (CloudRenderModes.TWO_IN_ADVANCE.equals(value)) {
                                    return Text.of("TWO_IN_ADVANCE");
                                } else if (CloudRenderModes.ONE_IN_ADVANCE.equals(value)) {
                                    return Text.of("ONE_IN_ADVANCE");
                                } else if (CloudRenderModes.CUSTOM_ALTITUDE.equals(value)) {
                                    return Text.of("CUSTOM_ALTITUDE");
                                } else if (CloudRenderModes.ALWAYS_RENDER.equals(value)) {
                                    return Text.of("ALWAYS_RENDER");
                                }
                                return Text.of("NEVER_RENDER");
                            }))
                    .setDefaultValue(CloudRenderModes.TWO_IN_ADVANCE)
                    .setTooltip(Text.of("This value determines when the cloud layer begins to render in high level of detail. It is only used if the cloud type is set to LOD."))
                    .setSuggestionMode(false)
                    .setSelections(Arrays.asList(CloudRenderModes.values()))
                    .setSaveConsumer(layer::setLodRenderMode)
                    .build());

            // LOD render distance
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startFloatField(Text.of("Custom LOD render altitude"), layer.getLodRenderDistance())
                    .setTooltip(Text.of("This value is only used if \"Custom altitude\" was selected as the LOD render mode."))
                    .setDefaultValue(0.0F)
                    .setSaveConsumer(layer::setLodRenderDistance)
                    .build());

            // Use smooth LODs
            layerAttributesList.get(layerNum).add(entryBuilder
                    .startBooleanToggle(Text.of("Smooth LODs"), layer.isUseSmoothLODs())
                    .setTooltip(Text.of("Gradually puffs up LOD clouds as the player approaches them so the LOD transition is not too rough. This option may use up more resources. Clouds begin to puff up at half distance from their layer."))
                    .setDefaultValue(false)
                    .setSaveConsumer(layer::setUseSmoothLODs)
                    .build());

            // Add to the counter to list next layer's attributes.
            layerNum++;
        }

        // * Create each layer subCategory with their own attribute list.
        for (int i = 0; i < layerAttributesList.size(); i++){
            layerList.add(i, entryBuilder.startSubCategory(Text.of(cloudLayerList.get(i).getName()), layerAttributesList.get(i)).build());
        }

        // * Add all layer subCategories to the main config subCategory.
        cloudSettings.addEntry(entryBuilder.startTextDescription(Text.of("--- Cloud layer individual configuration ---"))
                .setTooltip(Text.of("If you want to set up each cloud layer individually and in most detail, here is the place!"))
                .build());

        cloudSettings.addEntry(entryBuilder.startSubCategory(Text.of("Select a cloud layer:"), layerList)
                .build());
    }

    // Add empty layers if the layer amount setting is greater than the actual layer amount. Or remove layers if it is lower.
    private void updateLayerListEntries(List<CloudLayer> cloudLayerList, int parameterToChange){
        // * Reset to default
        if (resetToDefault){
            eanConfigFile.setLayerAmount(2);
            cloudLayerList.clear();
            cloudLayerList.add(new CloudLayer((float)eanConfigFile.getCurveStart(),CloudTypes.LOD,CloudRenderModes.ALWAYS_RENDER,0, CloudRenderModes.TWO_IN_ADVANCE,0, false));
            cloudLayerList.add(new CloudLayer((float)eanConfigFile.getCurveEnd(),CloudTypes.LOD,CloudRenderModes.ALWAYS_RENDER,0, CloudRenderModes.TWO_IN_ADVANCE,0, false));
            return;
        }

        // * Add to or subtract from the cloud layer list if the layer amount value has changed.
        int layerAmount = eanConfigFile.getLayerAmount();
        int layerAmountDifference = layerAmount - cloudLayerList.size();

        // If the layer amount set is greater than the actual amount, add layers.
        if (layerAmountDifference > 0){

            // Set new layer altitude based on the last layer altitude plus the cloud distance setting.
            float lastLayerAltitude;
            if (cloudLayerList.size() > 0){
                lastLayerAltitude = cloudLayerList.get(cloudLayerList.size()-1).getAltitude();
            } else {
                lastLayerAltitude = 192.0F;
            }

            // Add a layer for each layer there's missing from the total amount.
            for(int i = 0; i<layerAmountDifference;i++){
                lastLayerAltitude += eanConfigFile.getLayerDistance();
                cloudLayerList.add(new CloudLayer(lastLayerAltitude, eanConfigFile.getCloudType(), eanConfigFile.getRenderMode(), 0.0F, eanConfigFile.getLodRenderMode(), 0.0F, eanConfigFile.isUseSmoothLODs()));
            }
        } else if (layerAmountDifference < 0){
            while (cloudLayerList.size()>layerAmount) {
                cloudLayerList.remove(cloudLayerList.size()-1);
            }
        }

        // * Change a parameter of every cloud layer if it has been indicated.
        List<CloudLayer> auxList = new ArrayList<>();
        switch (parameterToChange) {
            case 0 -> {
                float altitude = eanConfigFile.getStackingAltitude();
                for (CloudLayer layer : cloudLayerList) {
                    altitude += eanConfigFile.getLayerDistance();
                    auxList.add(new CloudLayer(altitude, layer.getCloudType(), layer.getRenderMode(), layer.getCloudRenderDistance(), layer.getLodRenderMode(), layer.getLodRenderDistance(), layer.isUseSmoothLODs()));
                }
                cloudLayerList.clear();
                cloudLayerList.addAll(auxList);
            }
            case 1 -> {
                for (CloudLayer layer : cloudLayerList) {
                    auxList.add(new CloudLayer(layer.getAltitude(), eanConfigFile.getCloudType(), layer.getRenderMode(), layer.getCloudRenderDistance(), layer.getLodRenderMode(), layer.getLodRenderDistance(), layer.isUseSmoothLODs()));
                }
                cloudLayerList.clear();
                cloudLayerList.addAll(auxList);
            }
            case 2 -> {
                for (CloudLayer layer : cloudLayerList) {
                    auxList.add(new CloudLayer(layer.getAltitude(), layer.getCloudType(), eanConfigFile.getRenderMode(), layer.getCloudRenderDistance(), layer.getLodRenderMode(), layer.getLodRenderDistance(), layer.isUseSmoothLODs()));
                }
                cloudLayerList.clear();
                cloudLayerList.addAll(auxList);
            }
            case 3 -> {
                for (CloudLayer layer : cloudLayerList) {
                    auxList.add(new CloudLayer(layer.getAltitude(), layer.getCloudType(), layer.getRenderMode(), layer.getCloudRenderDistance(), eanConfigFile.getLodRenderMode(), layer.getLodRenderDistance(), layer.isUseSmoothLODs()));
                }
                cloudLayerList.clear();
                cloudLayerList.addAll(auxList);
            }
            case 4 ->{
                for (CloudLayer layer : cloudLayerList) {
                    auxList.add(new CloudLayer(layer.getAltitude(), layer.getCloudType(), layer.getRenderMode(), layer.getCloudRenderDistance(), layer.getLodRenderMode(), layer.getLodRenderDistance(), eanConfigFile.isUseSmoothLODs()));
                }
                cloudLayerList.clear();
                cloudLayerList.addAll(auxList);
            }
            default -> {
            }
        }
    }
}