package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.FloatValue;

public class VisualSettings extends ValueGroup {

    public VisualSettings(final ClientSettings parent) {
        super(parent, "Visual", "Visual related configs.");
    }

    public final BooleanValue customBobView = new BooleanValue(
            this,
            "Custom Bob View",
            "If enabled allows you to customize the bob view camera effect.",
            false
    );

    public final FloatValue customBobViewValue = new FloatValue(
            this,
            "Custom Bob View Value",
            "Here you can change the custom bob view value.",
            5.0f,
            0.0f,
            50.0f
    ).format("%.2f").visibleCondition(this.customBobView::getValue);

    public final FloatValue shieldAlpha = new FloatValue(
            this,
            "Shield Alpha",
            "Change the alpha of a shield.",
            1.0f,
            0.1f,
            1.0f
    ).format("%.2f");
    
}
