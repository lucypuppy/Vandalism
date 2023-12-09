package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.list.ModuleModeValue;

public class PhaseModule extends Module {

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
