package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.list.ModuleModeValue;

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
