package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;

@ModuleInfo(name = "Fast Use", description = "Allows you to make the use of items faster.", category = FeatureCategory.MISC)
public class FastUseModule extends Module {

    public final Value<Integer> itemUseCooldown = new SliderIntegerValue("Item Use Cooldown", "Here you can input the custom use cooldown value.", this, 0, 0, 3);

}
