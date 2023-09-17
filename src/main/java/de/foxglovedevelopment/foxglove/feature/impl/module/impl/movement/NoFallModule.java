package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.modes.nofall.VanillaModuleMode;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.list.ModuleModeValue;

public class NoFallModule extends Module {

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
