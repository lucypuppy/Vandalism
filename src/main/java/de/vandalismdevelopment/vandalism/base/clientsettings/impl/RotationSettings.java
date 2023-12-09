package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationGCD;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.EnumValue;

public class RotationSettings extends ValueCategory {

    public RotationSettings(final ClientSettings parent) {
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
