package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall.VanillaModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

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
