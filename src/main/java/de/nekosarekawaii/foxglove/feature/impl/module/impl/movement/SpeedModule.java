package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed.LongHopModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

@ModuleInfo(name = "Speed", description = "Makes you faster.", category = FeatureCategory.MOVEMENT)
public class SpeedModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current speed mode.", this,
            new LongHopModuleMode(this)
    );

}
