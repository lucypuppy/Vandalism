package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall.VanillaModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

@ModuleInfo(name = "No Fall", description = "Reduces or cancels your fall damage.", category = FeatureCategory.MOVEMENT)
public class NoFallModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current no-fall mode.", this,
            new VanillaModuleMode(this)
    );

}
