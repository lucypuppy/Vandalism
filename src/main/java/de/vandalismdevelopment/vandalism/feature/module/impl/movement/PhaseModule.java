package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.feature.module.value.ModuleModeValue;

public class PhaseModule extends AbstractModule {

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current phase mode.",
            this,
            new FallingBlockModuleMode(this)
    );

    public PhaseModule() {
        super(
                "Phase",
                "Lets you clip into or trough blocks.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

}
