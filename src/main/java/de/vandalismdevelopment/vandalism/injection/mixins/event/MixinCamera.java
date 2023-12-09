package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void vandalism$callCameraClipRaytraceEvent(final double desiredCameraDistance, final CallbackInfoReturnable<Double> cir) {
        final RenderListener.CameraClipRaytraceEvent cameraClipRaytraceEvent = new RenderListener.CameraClipRaytraceEvent();
        DietrichEvents2.global().postInternal(RenderListener.CameraClipRaytraceEvent.ID, cameraClipRaytraceEvent);
        if (cameraClipRaytraceEvent.isCancelled()) cir.setReturnValue(desiredCameraDistance);
    }

}
