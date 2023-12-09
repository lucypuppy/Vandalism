package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.speed.CubeCraftModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.speed.LongHopModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.speed.VerusHopModuleMode;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.list.ModuleModeValue;

public class SpeedModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current speed mode.",
            this,
            new LongHopModuleMode(this),
            new VerusHopModuleMode(this),
            new CubeCraftModuleMode(this)
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
