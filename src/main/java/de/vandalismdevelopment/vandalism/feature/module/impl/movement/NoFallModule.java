package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.nofall.VanillaModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.list.ModuleModeValue;

public class NoFallModule extends AbstractModule {

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current no fall mode.",
            this,
            new VanillaModuleMode(this)
    );

    public NoFallModule() {
        super(
                "No Fall",
                "Prevents some or all of the fall damage you get.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

}
