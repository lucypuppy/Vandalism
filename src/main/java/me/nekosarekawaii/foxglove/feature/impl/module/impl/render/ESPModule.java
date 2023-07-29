package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.ColorValue;

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
