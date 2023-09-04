package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;

public class FastUseModule extends Module {

    public final Value<Integer> itemUseCooldown = new SliderIntegerValue(
            "Item Use Cooldown",
            "Here you can input the custom use cooldown value.",
            this,
            0,
            0,
            3
    );

    public FastUseModule() {
        super(
                "Fast Use",
                "Allows you to use items faster.",
                FeatureCategory.MISC,
                false,
                false
        );
    }

}
