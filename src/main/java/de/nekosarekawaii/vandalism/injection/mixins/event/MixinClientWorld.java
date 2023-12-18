package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
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
    private void callSprintListener(final Entity entity, final CallbackInfo ci) {
        if (this.mc.player == entity) {
            final var event = new SprintListener.SprintEvent(entity.isSprinting());
            Vandalism.getInstance().getEventSystem().postInternal(SprintListener.SprintEvent.ID, event);

            if (event.force) {
                entity.setSprinting(event.sprinting);
            }
        }
    }

}
