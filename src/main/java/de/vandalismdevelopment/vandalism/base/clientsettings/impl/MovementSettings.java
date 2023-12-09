package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderFloatValue;

public class MovementSettings extends ValueCategory {

    public MovementSettings(final ClientSettings parent) {
        super("Movement", "Movement related configs.", parent);
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
