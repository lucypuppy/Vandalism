package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderFloatValue;

public class VisualSettings extends ValueCategory {

    public VisualSettings(final ClientSettings parent) {
        super("Visual", "Visual related configs.", parent);
    }

    public final Value<Boolean> customBobView = new BooleanValue(
            "Custom Bob View",
            "If enabled allows you to customize the bob view camera effect.",
            this,
            false
    );

    public final Value<Float> customBobViewValue = new SliderFloatValue(
            "Custom Bob View Value",
            "Here you can change the custom bob view value.-",
            this,
            5.0f,
            0.0f,
            50.0f,
            "%.2f"
    ).visibleConsumer(this.customBobView::getValue);

    public final Value<Float> shieldAlpha = new SliderFloatValue(
            "Shield Alpha",
            "Change the alpha of a shield.",
            this,
            1.0f,
            0.1f,
            1.0f,
            "%.2f"
    );
    
}
