package de.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.util.render.ColorUtils;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.ColorValue;

import java.awt.*;

@ModuleInfo(name = "ESP", description = "Allows you to see entities trough blocks.", category = FeatureCategory.RENDER)
public class ESPModule extends Module {

    public final Value<Color> outlineColor = new ColorValue(
            "Color",
            "The color of the outline.",
            this,
            ColorUtils.withAlpha(Color.MAGENTA, 200)
    );

}
