package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.CameraListener;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void injectClipToSpace(final double desiredCameraDistance, final CallbackInfoReturnable<Double> cir) {
        final CameraListener.CameraDistanceEvent event = new CameraListener.CameraDistanceEvent(desiredCameraDistance);
        DietrichEvents2.global().postInternal(CameraListener.CameraDistanceEvent.ID, event);
        if (event.isCancelled()) { //TODO: Does this make sense? - NekosAreKawaii
            cir.setReturnValue(event.desiredCameraDistance);
        }
    }

}
