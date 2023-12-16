package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;

public class MovementSettings extends ValueGroup {

    public final BooleanValue customizeRiptideBoostMultiplier = new BooleanValue(
            this,
            "Customize Riptide Boost Multiplier",
            "If activated shows you a slider to modify the riptide boost multiplier.",
            false
    );

    public final Value<Float> riptideBoostMultiplier = new FloatValue(
            this,
            "Riptide Boost Multiplier",
            "Lets you modify the riptide boost multiplier.",
            1.0f,
            -5.0f,
            5.0f
    ).visibleCondition(this.customizeRiptideBoostMultiplier::getValue);

    public MovementSettings(final ClientSettings parent) {
        super(parent, "Movement", "Movement related settings.");
    }

}
