package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;

@ModuleInfo(name = "Fast Use", description = "Allows you to make the use of items faster.", category = FeatureCategory.MISC)
public class FastUseModule extends Module {

    public final Value<Integer> itemUseCooldown = new SliderIntegerValue("Item Use Cooldown", "Here you can input the custom use cooldown value.", this, 0, 0, 3);

}
