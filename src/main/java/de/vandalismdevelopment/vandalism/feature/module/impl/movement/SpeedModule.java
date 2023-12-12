package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.CubeCraftModuleMode;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.LongHopModuleMode;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed.VerusHopModuleMode;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleModeValue;

public class SpeedModule extends AbstractModule {

    private final ModuleModeValue<SpeedModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current speed mode.",
            new LongHopModuleMode(this),
            new VerusHopModuleMode(this),
            new CubeCraftModuleMode(this)
    );

    public SpeedModule() {
        super("Speed", "Makes your on-ground movement faster or better.", Category.MOVEMENT);
    }

}
