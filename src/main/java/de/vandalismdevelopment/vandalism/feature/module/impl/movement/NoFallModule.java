package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.nofall.VanillaModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.feature.module.value.ModuleModeValue;

public class NoFallModule extends AbstractModule {

    private final ModuleModeValue<NoFallModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current no fall mode.",
            new VanillaModuleMode(this)
    );

    public NoFallModule() {
        super("No Fall", "Prevents some or all of the fall damage you get.", Category.MOVEMENT);
    }

}
