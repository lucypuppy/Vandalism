package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.util.rotation.RotationGCD;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.EnumValue;

public class RotationCategory extends ValueCategory {

    public RotationCategory(final MainConfig parent) {
        super("Rotation", "Rotation related settings.", parent);
    }

    public final Value<Boolean> rotateBack = new BooleanValue(
            "Rotate Back",
            "Enable / Disable smooth rotate back.",
            this,
            true
    );

    public final EnumValue<RotationGCD> gcdMode = new EnumValue<>(
            "GCD Mode",
            "Change the GCD Mode.",
            this,
            RotationGCD.REAL,
            RotationGCD.NONE
    );
    
}
