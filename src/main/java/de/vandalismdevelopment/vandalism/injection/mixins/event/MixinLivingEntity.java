package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
    private void vandalism$callMoveFlyingEvent(final Args args) {
        final double velocityX = args.get(0), velocityY = args.get(1), velocityZ = args.get(2);
        final MovementListener.MoveFlyingEvent moveFlyingEvent = new MovementListener.MoveFlyingEvent(velocityX, velocityY, velocityZ);
        DietrichEvents2.global().postInternal(MovementListener.MoveFlyingEvent.ID, moveFlyingEvent);
        args.set(0, moveFlyingEvent.velocityX);
        args.set(1, moveFlyingEvent.velocityY);
        args.set(2, moveFlyingEvent.velocityZ);
    }

}
