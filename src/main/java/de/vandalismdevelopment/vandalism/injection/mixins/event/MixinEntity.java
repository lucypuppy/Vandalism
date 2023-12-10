package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements MinecraftWrapper {

    @Shadow
    private static Vec3d movementInputToVelocity(final Vec3d movementInput, final float speed, final float yaw) {
        return null;
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0.05000000074505806), method = "pushAwayFrom")
    private double vandalism$callEntityPushEvent(final double constant) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final MovementListener.EntityPushEvent entityPushEvent = new MovementListener.EntityPushEvent(constant);
            DietrichEvents2.global().postInternal(MovementListener.EntityPushEvent.ID, entityPushEvent);
            if (entityPushEvent.isCancelled()) return 0;
            return entityPushEvent.value;
        }
        return constant;
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean vandalism$callFluidPushEvent1(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final MovementListener.FluidPushEvent fluidPushEvent = new MovementListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(MovementListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean vandalism$callFluidPushEvent2(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final MovementListener.FluidPushEvent fluidPushEvent = new MovementListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(MovementListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F"))
    private float vandalism$callStepEvent(final Entity instance) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final MovementListener.StepEvent stepEvent = new MovementListener.StepEvent(instance.getStepHeight());
            DietrichEvents2.global().postInternal(MovementListener.StepEvent.ID, stepEvent);
            return stepEvent.stepHeight;
        }
        return instance.getStepHeight();
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void vandalism$callStepSuccessEvent(final Vec3d movement, final CallbackInfoReturnable<Vec3d> cir) {
        if (this.mc.player != ((Entity) (Object) this)) {
            return;
        }
        final MovementListener.StepSuccessEvent stepSuccessEvent = new MovementListener.StepSuccessEvent(movement, cir.getReturnValue());
        DietrichEvents2.global().postInternal(MovementListener.StepSuccessEvent.ID, stepSuccessEvent);
        cir.setReturnValue(stepSuccessEvent.adjustMovementForCollisions);
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d vandalism$callStrafeEvent(final Vec3d movementInput, final float speed, final float yaw) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final MovementListener.StrafeEvent strafeEvent = new MovementListener.StrafeEvent(movementInput, speed, yaw);
            DietrichEvents2.global().postInternal(MovementListener.StrafeEvent.ID, strafeEvent);
            return movementInputToVelocity(strafeEvent.movementInput, strafeEvent.speed, strafeEvent.yaw);
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

}