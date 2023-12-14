package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.player.SprintListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
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
        if (this.mc.player != entity) return;
        final SprintListener.SprintEvent sprintEvent = new SprintListener.SprintEvent(entity.isSprinting());
        DietrichEvents2.global().postInternal(SprintListener.SprintEvent.ID, sprintEvent);
        if (sprintEvent.force) {
            entity.setSprinting(sprintEvent.sprinting);
        }
    }

}
