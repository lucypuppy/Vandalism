package de.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.util.rotation.RotationPriority;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;

@ModuleInfo(name = "Test", description = "This is just a module for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public class TestModule extends Module {

    @Override
    protected void onEnable() {
        if(mc.player == null) {
            return;
        }

        Foxglove.getInstance().getRotationListener().setRotation(new Rotation(mc.player.yaw - 180f, 20), 20, RotationPriority.HIGH);
    }

    @Override
    protected void onDisable() {
        Foxglove.getInstance().getRotationListener().setRotation(null, 20, RotationPriority.HIGH);
    }

}
