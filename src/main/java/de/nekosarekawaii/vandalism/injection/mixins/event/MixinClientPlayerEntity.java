package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.StateTypes;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

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
