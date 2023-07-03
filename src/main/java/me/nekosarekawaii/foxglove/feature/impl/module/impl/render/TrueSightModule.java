package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;

@ModuleInfo(name = "True Sight", description = "Makes invisible blocks and entities visible.", category = FeatureCategory.RENDER)
public class TrueSightModule extends Module {

    public final BooleanValue blocks = new BooleanValue("Blocks", "Makes invisible blocks visible.", this, true);
    public final BooleanValue entities = new BooleanValue("Entities", "Makes invisible entities visible.", this, true);


}
