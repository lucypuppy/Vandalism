package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
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
        final MovementListener.MoveInputEvent moveInputEvent = new MovementListener.MoveInputEvent(
                this.movementForward,
                this.movementSideways,
                slowDown,
                slowDownFactor
        );
        DietrichEvents2.global().postInternal(MovementListener.MoveInputEvent.ID, moveInputEvent);
        this.movementForward = moveInputEvent.movementForward;
        this.movementSideways = moveInputEvent.movementSideways;
    }

}
