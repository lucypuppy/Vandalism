package de.foxglovedevelopment.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.CameraClipRaytraceListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;

public class CameraNoClipModule extends Module implements CameraClipRaytraceListener {

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
        DietrichEvents2.global().subscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    public void onCameraClipRaytrace(final CameraClipRaytraceEvent event) {
        event.cancel();
    }

}
