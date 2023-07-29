package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall.Grim117ModuleMode;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall.VanillaModuleMode;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;

@ModuleInfo(name = "No Fall", description = "Reduces or cancels your fall damage.", category = FeatureCategory.MOVEMENT)
public class NoFallModule extends Module {

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current nofall mode.", this,
            new VanillaModuleMode(this),
            new Grim117ModuleMode(this)
    );

}
