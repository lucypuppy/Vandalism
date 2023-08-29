package de.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.util.rotation.RotationListener;
import de.nekosarekawaii.foxglove.util.rotation.RotationPriority;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.ValueCategory;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import de.nekosarekawaii.foxglove.value.values.number.IntegerValue;

@ModuleInfo(name = "Test", description = "This is just a module for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public class TestModule extends Module {

    @Override
    protected void onEnable() {
        Foxglove.getInstance().getRotationListener().setRotation(new Rotation(20, 20), 100, RotationPriority.HIGH);
    }

    @Override
    protected void onDisable() {
        Foxglove.getInstance().getRotationListener().setRotation(null, 100, RotationPriority.HIGH);
    }

}
