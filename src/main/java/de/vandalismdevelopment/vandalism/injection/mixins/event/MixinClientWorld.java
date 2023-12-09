package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientWorld.class)
public abstract class MixinClientWorld implements MinecraftWrapper {

    @Inject(method = "tickEntity", at = @At(value = "HEAD"))
    private void vandalism$callSprintEvent(final Entity entity, final CallbackInfo ci) {
        if (this.player() != entity) return;
        final MovementListener.SprintEvent sprintEvent = new MovementListener.SprintEvent(entity.isSprinting());
        DietrichEvents2.global().postInternal(MovementListener.SprintEvent.ID, sprintEvent);
        if (sprintEvent.force) {
            entity.setSprinting(sprintEvent.sprinting);
        }
    }

}
