package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.rclasses.common.ColorUtils;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

import java.awt.*;

public class ESPModule extends AbstractModule {

    public final Value<Color> outlineColor = new ColorValue(
            this,
            "Color",
            "The color of the outline.",
            ColorUtils.withAlpha(Color.MAGENTA, 200)
    );

    public ESPModule() {
        super("ESP", "Lets you see blocks or entities trough blocks.", Category.RENDER);
    }

}
