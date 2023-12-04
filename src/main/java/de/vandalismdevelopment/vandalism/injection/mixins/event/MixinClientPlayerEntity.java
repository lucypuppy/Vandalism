package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends LivingEntity {

    protected MixinClientPlayerEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
    }

    @Unique
    private MovementListener.SprintEvent vandalism$createAndCallSprintEvent(final boolean original) {
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(original);
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        return sprintEvent;
    }

    @Inject(method = "tickMovement", at = @At(value = "RETURN"))
    private void vandalism$callSprintEvent1(final CallbackInfo ci) {
        final MovementListener.SprintEvent sprintEvent = this.vandalism$createAndCallSprintEvent(this.isSprinting());
        if (sprintEvent.bypass) {
            this.setSprinting(sprintEvent.sprinting);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 0))
    private boolean vandalism$callSprintEvent2(final KeyBinding instance) {
        return this.vandalism$createAndCallSprintEvent(instance.isPressed()).sprinting;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    private boolean vandalism$callSprintEvent3(final KeyBinding instance) {
        return this.vandalism$createAndCallSprintEvent(instance.isPressed()).sprinting;
    }

}
