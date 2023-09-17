package de.foxglovedevelopment.foxglove.feature.impl.module.impl.render;

import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.util.render.ColorUtils;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.ColorValue;

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
