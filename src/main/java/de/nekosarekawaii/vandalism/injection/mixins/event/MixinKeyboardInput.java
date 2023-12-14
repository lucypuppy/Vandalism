package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.player.MoveInputListener;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input {

    @Inject(method = "tick", at = @At("RETURN"))
    private void vandalism$callMoveInputEvent(final boolean slowDown, final float slowDownFactor, final CallbackInfo ci) {
        final MoveInputListener.MoveInputEvent moveInputEvent = new MoveInputListener.MoveInputEvent(
                this.movementForward,
                this.movementSideways,
                slowDown,
                slowDownFactor
        );
        DietrichEvents2.global().postInternal(MoveInputListener.MoveInputEvent.ID, moveInputEvent);
        this.movementForward = moveInputEvent.movementForward;
        this.movementSideways = moveInputEvent.movementSideways;
    }

}
