package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.CameraDistanceListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;

@ModuleInfo(name = "Camera No Clip", description = "Allows you to clip through blocks while in camera mode.", category = FeatureCategory.MISC)
public class CameraNoClipModule extends Module implements CameraDistanceListener {

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
