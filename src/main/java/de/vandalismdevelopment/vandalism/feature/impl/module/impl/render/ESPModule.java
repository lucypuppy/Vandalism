package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.rclasses.common.ColorUtils;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.RenderUtil;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.ColorValue;

import java.awt.*;

public class ESPModule extends Module {

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
