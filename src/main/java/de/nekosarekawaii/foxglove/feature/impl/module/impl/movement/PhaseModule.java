package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.phase.FallingBlockModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

@ModuleInfo(name = "Phase", description = "Walk trough blocks.", category = FeatureCategory.MOVEMENT)
public class PhaseModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current phase mode.", this,
            new FallingBlockModuleMode(this)
    );

}
