package de.vandalismdevelopment.vandalism.feature.module.impl.misc;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderIntegerValue;

public class FastUseModule extends AbstractModule {

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
