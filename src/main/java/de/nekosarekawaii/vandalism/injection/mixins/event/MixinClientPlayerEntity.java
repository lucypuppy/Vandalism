package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.StateTypes;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.base.event.player.SprintListener;
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
    private boolean callSprintListener_at_1(final KeyBinding instance) {
        final var event = new SprintListener.SprintEvent(instance.isPressed());
        Vandalism.getInstance().getEventSystem().postInternal(SprintListener.SprintEvent.ID, event);
        return event.sprinting;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean callSprintListener_at_2(final KeyBinding instance) {
        final var event = new SprintListener.SprintEvent(instance.isPressed());
        Vandalism.getInstance().getEventSystem().postInternal(SprintListener.SprintEvent.ID, event);
        return event.sprinting;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void callMotionListener_pre(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.PRE);
        Vandalism.getInstance().getEventSystem().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

    @Inject(method = "sendMovementPackets()V", at = @At("TAIL"))
    private void callMotionListener_post(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.POST);
        Vandalism.getInstance().getEventSystem().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

}
