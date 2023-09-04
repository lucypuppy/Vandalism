package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed.LongHopModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

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
