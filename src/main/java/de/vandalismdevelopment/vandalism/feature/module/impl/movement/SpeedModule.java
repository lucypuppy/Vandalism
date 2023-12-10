package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.CubeCraftModuleMode;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.LongHopModuleMode;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.VerusHopModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.list.ModuleModeValue;

public class SpeedModule extends AbstractModule {

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
