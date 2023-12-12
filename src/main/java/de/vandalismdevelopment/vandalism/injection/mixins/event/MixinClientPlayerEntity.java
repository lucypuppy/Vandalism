package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.StateTypes;
import de.vandalismdevelopment.vandalism.base.event.entity.MotionListener;
import de.vandalismdevelopment.vandalism.base.event.player.SprintListener;
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
        final var sprintEvent = new SprintListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(SprintListener.SprintEvent.ID, sprintEvent);
        return sprintEvent.sprinting;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean vandalism$callSprintEvent2(final KeyBinding instance) {
        final var sprintEvent = new SprintListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(SprintListener.SprintEvent.ID, sprintEvent);
        return sprintEvent.sprinting;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void vandalism$callPreMotionEvent(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.PRE);
        DietrichEvents2.global().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

    @Inject(method = "sendMovementPackets()V", at = @At("TAIL"))
    private void vandalism$callPostMotionEvent(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.POST);
        DietrichEvents2.global().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

}
