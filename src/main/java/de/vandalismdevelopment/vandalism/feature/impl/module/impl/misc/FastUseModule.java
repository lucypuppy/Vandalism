package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;

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
