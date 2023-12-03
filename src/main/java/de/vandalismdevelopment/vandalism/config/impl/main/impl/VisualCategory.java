package de.vandalismdevelopment.vandalism.config.impl.main.impl;

import de.vandalismdevelopment.vandalism.config.impl.main.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;

public class VisualCategory extends ValueCategory {

    public VisualCategory(final MainConfig parent) {
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
