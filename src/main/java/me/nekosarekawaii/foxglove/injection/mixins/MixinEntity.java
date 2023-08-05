package me.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.EntityPushListener;
import me.nekosarekawaii.foxglove.event.FluidPushListener;
import me.nekosarekawaii.foxglove.event.StepListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean redirectUpdateWaterState(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final FluidPushListener.FluidPushEvent fluidPushEvent = new FluidPushListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidPushListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean redirectCheckWaterState(final Entity instance, final TagKey<Fluid> tag, final double speed) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final FluidPushListener.FluidPushEvent fluidPushEvent = new FluidPushListener.FluidPushEvent(speed);
            DietrichEvents2.global().postInternal(FluidPushListener.FluidPushEvent.ID, fluidPushEvent);
            if (fluidPushEvent.isCancelled()) return false;
            else return instance.updateMovementInFluid(tag, fluidPushEvent.speed);
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0.05000000074505806), method = "pushAwayFrom")
    private double modifyPushVelocity(final double constant) {
        if (MinecraftClient.getInstance().player == ((Entity) (Object) this)) {
            final EntityPushListener.EntityPushEvent entityPushEvent = new EntityPushListener.EntityPushEvent(constant);
            DietrichEvents2.global().postInternal(EntityPushListener.EntityPushEvent.ID, entityPushEvent);
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

}
