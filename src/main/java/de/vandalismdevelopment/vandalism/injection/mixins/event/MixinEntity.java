package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.EntityListener;
import de.vandalismdevelopment.vandalism.event.FluidListener;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.event.StepListener;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntity implements MinecraftWrapper {

    @Shadow
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0.05000000074505806), method = "pushAwayFrom")
    private double vandalism$callEntityPushEvent(final double constant) {
        if (this.player() == ((Entity) (Object) this)) {
            final EntityListener.EntityPushEvent entityPushEvent = new EntityListener.EntityPushEvent(constant);
            DietrichEvents2.global().postInternal(EntityListener.EntityPushEvent.ID, entityPushEvent);
            if (entityPushEvent.isCancelled()) return 0;
            return entityPushEvent.value;
        }
        return constant;
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean vandalism$callFluidPushEvent1(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (this.player() == ((Entity) (Object) this)) {
            final FluidListener.FluidPushEvent fluidPushEvent = new FluidListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean vandalism$callFluidPushEvent2(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (this.player() == ((Entity) (Object) this)) {
            final FluidListener.FluidPushEvent fluidPushEvent = new FluidListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F"))
    private float vandalism$callStepEvent(final Entity entity) {
        if (this.player() == ((Entity) (Object) this)) {
            final StepListener.StepEvent stepEvent = new StepListener.StepEvent(entity.getStepHeight());
            DietrichEvents2.global().postInternal(StepListener.StepEvent.ID, stepEvent);
            return stepEvent.stepHeight;
        }
        return entity.getStepHeight();
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookVelocity(Vec3d movementInput, float speed, float yaw) {
        if (this.player() == ((Entity) (Object) this)) {
            final MovementListener.StrafeEvent strafeEvent = new MovementListener.StrafeEvent(movementInput, speed, yaw, movementInputToVelocity(movementInput, speed, yaw));
            DietrichEvents2.global().postInternal(MovementListener.StrafeEvent.ID, strafeEvent);
            return strafeEvent.velocity;
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

}