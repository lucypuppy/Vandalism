package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;

public class PhaseModule extends AbstractModule {

    private final ModuleModeValue<PhaseModule> mode = new ModuleModeValue<>(this, "Mode", "The current phase mode.", new FallingBlockModuleMode(this));

    public PhaseModule() {
        super("Phase", "Lets you clip into or trough blocks.", Category.MOVEMENT);
    }

}
