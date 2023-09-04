package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

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
