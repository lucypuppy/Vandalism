package de.foxglovedevelopment.foxglove.feature.impl.module.impl.misc;

import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.number.slider.SliderIntegerValue;

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
