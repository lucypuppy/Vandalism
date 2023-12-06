package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper {

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
    private void vandalism$callMoveFlyingEvent(final Args args) {
        if (this.player() != ((LivingEntity) (Object) this)) return;
        final double sidewaysSpeed = args.get(0), upwardSpeed = args.get(1), forwardSpeed = args.get(2);
        final MovementListener.MoveFlyingEvent moveFlyingEvent = new MovementListener.MoveFlyingEvent(sidewaysSpeed, upwardSpeed, forwardSpeed);
        DietrichEvents2.global().postInternal(MovementListener.MoveFlyingEvent.ID, moveFlyingEvent);
        args.set(0, moveFlyingEvent.sidewaysSpeed);
        args.set(1, moveFlyingEvent.upwardSpeed);
        args.set(2, moveFlyingEvent.forwardSpeed);
    }

}
