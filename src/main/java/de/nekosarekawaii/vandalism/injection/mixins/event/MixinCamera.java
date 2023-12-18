package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.CameraClipRaytraceListener;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void callCameraClipRaytraceListener(final double desiredCameraDistance, final CallbackInfoReturnable<Double> cir) {
        final var event = new CameraClipRaytraceListener.CameraClipRaytraceEvent();
        Vandalism.getInstance().getEventSystem().postInternal(CameraClipRaytraceListener.CameraClipRaytraceEvent.ID, event);

        if (event.isCancelled()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }

}
