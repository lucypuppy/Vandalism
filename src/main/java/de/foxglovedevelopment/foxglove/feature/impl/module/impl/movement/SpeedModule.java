package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.modes.speed.LongHopModuleMode;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.list.ModuleModeValue;

public class SpeedModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current speed mode.",
            this,
            new LongHopModuleMode(this)
    );

    public SpeedModule() {
        super(
                "Speed",
                "Makes your on-ground movement faster or better.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

}
