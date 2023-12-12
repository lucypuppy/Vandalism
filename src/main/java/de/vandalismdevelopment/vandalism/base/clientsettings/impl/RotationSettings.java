package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationGCD;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.selection.EnumModeValue;

public class RotationSettings extends ValueGroup {

    public final BooleanValue rotateBack = new BooleanValue(
            this,
            "Rotate Back",
            "Enable / Disable smooth rotate back.",
            true
    );

    public final EnumModeValue<RotationGCD> gcdMode = new EnumModeValue<>(
            this,
            "GCD Mode",
            "Change the GCD Mode.",
            RotationGCD.REAL,
            RotationGCD.NONE
    );

    public RotationSettings(final ClientSettings parent) {
        super(parent, "Rotation", "Rotation related settings.");
    }

}
