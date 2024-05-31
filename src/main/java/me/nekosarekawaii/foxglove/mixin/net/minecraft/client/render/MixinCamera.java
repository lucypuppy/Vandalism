package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.CameraDistanceListener;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void injectClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        final var event = new CameraDistanceListener.CameraDistanceEvent(desiredCameraDistance);
        DietrichEvents2.global().postInternal(CameraDistanceListener.CameraDistanceEvent.ID, event);

        if (event.isCancelled()) {
            cir.setReturnValue(event.desiredCameraDistance);
        }
    }
}
