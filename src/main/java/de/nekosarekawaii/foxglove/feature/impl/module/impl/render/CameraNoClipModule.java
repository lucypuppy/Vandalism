package de.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.CameraListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;

public class CameraNoClipModule extends Module implements CameraListener {

    public CameraNoClipModule() {
        super(
                "Camera No Clip",
                "Disables camera block collision and lets you see trough blocks.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

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
