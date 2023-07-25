package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed.LongHopMode;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.list.ModeValue;

@ModuleInfo(name = "Speed", description = "Makes you faster.", category = FeatureCategory.MOVEMENT)
public class SpeedModule extends Module {

    private final Value<String> mode = new ModeValue<>("Mode", "The current speed mode.", this,
            new LongHopMode(this)
    );

}
