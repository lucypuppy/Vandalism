package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderFloatValue;

public class MovementCategory extends ValueCategory {

    public MovementCategory(final MainConfig parent) {
        super("Movement", "Movement related settings.", parent);
    }

    public final Value<Boolean> customizeRiptideBoostMultiplier = new BooleanValue(
            "Customize Riptide Boost Multiplier",
            "If enabled shows you a slider to modify the riptide boost multiplier.",
            this,
            false
    );

    public final Value<Float> riptideBoostMultiplier = new SliderFloatValue(
            "Riptide Boost Multiplier",
            "Lets you modify the riptide boost multiplier.",
            this,
            1.0f,
            -5.0f,
            5.0f
    ).visibleConsumer(this.customizeRiptideBoostMultiplier::getValue);

}
