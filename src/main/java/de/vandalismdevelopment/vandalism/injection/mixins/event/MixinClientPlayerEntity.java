package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean vandalism$callSprintEvent1(final KeyBinding instance) {
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        return sprintEvent.sprinting;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean vandalism$callSprintEvent2(final KeyBinding instance) {
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        return sprintEvent.sprinting;
    }

    @Inject(method = "sendMovementPackets()V", at = @At("HEAD"))
    private void vandalism$callPreMotionEvent(final CallbackInfo ci) {
        final MovementListener.MotionEvent motionEvent = new MovementListener.MotionEvent(MovementListener.MotionEventState.PRE);
        DietrichEvents2.global().postInternal(MovementListener.MotionEvent.ID, motionEvent);
    }

    @Inject(method = "sendMovementPackets()V", at = @At("TAIL"))
    private void vandalism$callPostMotionEvent(final CallbackInfo ci) {
        final MovementListener.MotionEvent motionEvent = new MovementListener.MotionEvent(MovementListener.MotionEventState.POST);
        DietrichEvents2.global().postInternal(MovementListener.MotionEvent.ID, motionEvent);
    }

}
