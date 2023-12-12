package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;

public class CameraNoClipModule extends AbstractModule implements RenderListener {

    public CameraNoClipModule() {
        super("Camera No Clip", "Disables camera block collision and lets you see trough blocks.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    public void onCameraClipRaytrace(final CameraClipRaytraceEvent event) {
        event.cancel();
    }

}
