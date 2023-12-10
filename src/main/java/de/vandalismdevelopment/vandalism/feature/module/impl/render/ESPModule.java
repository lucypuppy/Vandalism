package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.rclasses.common.ColorUtils;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.ColorValue;

import java.awt.*;

public class ESPModule extends AbstractModule {

    public final Value<Color> outlineColor = new ColorValue(
            "Color",
            "The color of the outline.",
            this,
            ColorUtils.withAlpha(Color.MAGENTA, 200)
    );

    public ESPModule() {
        super(
                "ESP",
                "Lets you see blocks or entities trough blocks.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

}
