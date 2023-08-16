package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.CameraListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;

@ModuleInfo(name = "Camera No Clip", description = "Allows you to clip through blocks while in camera mode.", category = FeatureCategory.MISC)
public class CameraNoClipModule extends Module implements CameraListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(CameraDistanceEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(CameraDistanceEvent.ID, this);
    }

    @Override
    public void onCameraDistanceGet(final CameraDistanceEvent event) {
        event.cancel();
    }
}
