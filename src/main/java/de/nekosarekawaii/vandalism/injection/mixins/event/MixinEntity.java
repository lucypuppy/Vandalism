package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.EntityPushListener;
import de.nekosarekawaii.vandalism.base.event.entity.FluidPushListener;
import de.nekosarekawaii.vandalism.base.event.entity.StepListener;
import de.nekosarekawaii.vandalism.base.event.entity.StepSuccessListener;
import de.nekosarekawaii.vandalism.base.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
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
    private double callEntityPushListener(final double constant) {
        if (this.mc.player == (Object) this) {
            final EntityPushListener.EntityPushEvent entityPushEvent = new EntityPushListener.EntityPushEvent(constant);
            Vandalism.getEventSystem().postInternal(EntityPushListener.EntityPushEvent.ID, entityPushEvent);
            if (entityPushEvent.isCancelled()) return 0;
            return entityPushEvent.value;
        }
        return constant;
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean callFluidPushListener_Water(final Entity instance, final TagKey<Fluid> tag, double speed) {
        if (this.mc.player == (Object) this) {
            final var event = new FluidPushListener.FluidPushEvent(speed);
            Vandalism.getEventSystem().postInternal(FluidPushListener.FluidPushEvent.ID, event);

            if (event.isCancelled()) {
                return false;
            }
            speed = event.speed;
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean callFluidPushListener_Collision(final Entity instance, final TagKey<Fluid> tag, double speed) {
        if (this.mc.player == (Object) this) {
            final var event = new FluidPushListener.FluidPushEvent(speed);
            Vandalism.getEventSystem().postInternal(FluidPushListener.FluidPushEvent.ID, event);
            if (event.isCancelled()) {
                return false;
            }
            speed = event.speed;
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F"))
    private float callStepListener(final Entity instance) {
        if (this.mc.player == (Object) this) {
            final var event = new StepListener.StepEvent(instance.getStepHeight());
            Vandalism.getEventSystem().postInternal(StepListener.StepEvent.ID, event);

            return event.stepHeight;
        }
        return instance.getStepHeight();
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void callStepSuccessListener(final Vec3d movement, final CallbackInfoReturnable<Vec3d> cir) {
        if (this.mc.player == (Object) this) {
            final var event = new StepSuccessListener.StepSuccessEvent(movement, cir.getReturnValue());
            Vandalism.getEventSystem().postInternal(StepSuccessListener.StepSuccessEvent.ID, event);

            cir.setReturnValue(event.adjustMovementForCollisions);
        }
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d callStrafeListener(final Vec3d movementInput, final float speed, final float yaw) {
        if (this.mc.player == (Object) this) {
            final var event = new StrafeListener.StrafeEvent(movementInput, speed, yaw);
            Vandalism.getEventSystem().postInternal(StrafeListener.StrafeEvent.ID, event);

            return movementInputToVelocity(event.movementInput, event.speed, event.yaw);
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

}
