package de.vandalismdevelopment.vandalism.config.impl.main.impl;

import de.vandalismdevelopment.vandalism.config.impl.main.MainConfig;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationGCD;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.EnumValue;

public class RotationCategory extends ValueCategory {

    public RotationCategory(final MainConfig parent) {
        super("Rotation", "Rotation related configs.", parent);
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
