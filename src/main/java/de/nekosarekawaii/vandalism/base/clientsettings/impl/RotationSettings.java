package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.integration.rotation.RotationGCD;

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
