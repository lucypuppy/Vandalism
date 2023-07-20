package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;

@ModuleInfo(name = "Fast Place", description = "Allows you to place blocks faster.", category = FeatureCategory.MISC)
public class FastPlaceModule extends Module {

    public final Value<Integer> itemUseCooldown = new SliderIntegerValue("Item Use Cooldown", "awdawdawda", this, 0, 0, 4);

}
