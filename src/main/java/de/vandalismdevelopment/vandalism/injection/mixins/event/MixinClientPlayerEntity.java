package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean vandalism$callSprintEvent1(final KeyBinding instance) {
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        return instance.isPressed() && !sprintEvent.sprinting;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean vandalism$callSprintEvent2(final KeyBinding instance) {
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(instance.isPressed());
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        return instance.isPressed() && sprintEvent.sprinting;
    }

}
