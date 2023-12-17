package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.CameraClipRaytraceListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class CameraNoClipModule extends AbstractModule implements CameraClipRaytraceListener {

    public CameraNoClipModule() {
        super("Camera No Clip", "Disables camera block collision and lets you see trough blocks.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(CameraClipRaytraceEvent.ID, this);
    }

    @Override
    public void onCameraClipRaytrace(final CameraClipRaytraceEvent event) {
        event.cancel();
    }

}
