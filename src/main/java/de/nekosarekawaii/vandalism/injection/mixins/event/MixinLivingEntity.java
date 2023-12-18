package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.MoveFlyingListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper {

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
    private void callMoveFlyingListener(final Args args) {
        if (this.mc.player == (Object) this) {
            final var event = new MoveFlyingListener.MoveFlyingEvent(args.get(0), args.get(1), args.get(2));
            Vandalism.getInstance().getEventSystem().postInternal(MoveFlyingListener.MoveFlyingEvent.ID, event);

            args.set(0, event.sidewaysSpeed);
            args.set(1, event.upwardSpeed);
            args.set(2, event.forwardSpeed);
        }
    }

}
