package me.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.CameraDistanceListener;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void injectClipToSpace(final double desiredCameraDistance, final CallbackInfoReturnable<Double> cir) {
        final var event = new CameraDistanceListener.CameraDistanceEvent(desiredCameraDistance);
        DietrichEvents2.global().postInternal(CameraDistanceListener.CameraDistanceEvent.ID, event);
        if (event.isCancelled()) { //TODO: Does this make sense? - NekosAreKawaii
            cir.setReturnValue(event.desiredCameraDistance);
        }
    }

}
