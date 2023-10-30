package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.EntityListener;
import de.vandalismdevelopment.vandalism.event.FluidListener;
import de.vandalismdevelopment.vandalism.event.StepListener;
import de.vandalismdevelopment.vandalism.util.rotation.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean redirectUpdateWaterState(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final FluidListener.FluidPushEvent fluidPushEvent = new FluidListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean redirectCheckWaterState(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final FluidListener.FluidPushEvent fluidPushEvent = new FluidListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0.05000000074505806), method = "pushAwayFrom")
    private double modifyPushVelocity(final double constant) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final EntityListener.EntityPushEvent entityPushEvent = new EntityListener.EntityPushEvent(constant);
            DietrichEvents2.global().postInternal(EntityListener.EntityPushEvent.ID, entityPushEvent);
            if (entityPushEvent.isCancelled()) return 0;
            return entityPushEvent.value;
        }
        return constant;
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F"))
    private float injectAdjustMovementForCollisions(final Entity entity) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final StepListener.StepEvent stepEvent = new StepListener.StepEvent(entity.getStepHeight());
            DietrichEvents2.global().postInternal(StepListener.StepEvent.ID, stepEvent);
            return stepEvent.stepHeight;
        }
        return entity.getStepHeight();
    }

    @ModifyVariable(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float modifyYaw(final float yaw) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return yaw;
    }

    @ModifyVariable(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyPitch(final float pitch) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getPitch();
        }
        return pitch;
    }

}
