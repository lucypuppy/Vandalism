package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;

public class CameraNoClipModule extends Module implements RenderListener {

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
