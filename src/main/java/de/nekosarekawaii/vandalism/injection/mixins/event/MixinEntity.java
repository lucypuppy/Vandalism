/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.EntityRemoveListener;
import de.nekosarekawaii.vandalism.event.player.*;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements MinecraftWrapper {

    @Shadow
    private static Vec3d movementInputToVelocity(final Vec3d movementInput, final float speed, final float yaw) {
        return null;
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0.05000000074505806), method = "pushAwayFrom")
    private double callEntityPushListener(final double constant) {
        if (mc.player == (Object) this) {
            final EntityPushListener.EntityPushEvent entityPushEvent = new EntityPushListener.EntityPushEvent(constant);
            Vandalism.getInstance().getEventSystem().callExceptionally(EntityPushListener.EntityPushEvent.ID, entityPushEvent);
            if (entityPushEvent.isCancelled()) return 0;
            return entityPushEvent.value;
        }
        return constant;
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean callFluidPushListener_Water(final Entity instance, final TagKey<Fluid> tag, double speed) {
        if (mc.player == (Object) this) {
            final FluidPushListener.FluidPushEvent event = new FluidPushListener.FluidPushEvent(speed);
            Vandalism.getInstance().getEventSystem().callExceptionally(FluidPushListener.FluidPushEvent.ID, event);
            if (event.isCancelled()) {
                return false;
            }
            speed = event.speed;
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean callFluidPushListener_Collision(final Entity instance, final TagKey<Fluid> tag, double speed) {
        if (mc.player == (Object) this) {
            final FluidPushListener.FluidPushEvent event = new FluidPushListener.FluidPushEvent(speed);
            Vandalism.getInstance().getEventSystem().callExceptionally(FluidPushListener.FluidPushEvent.ID, event);
            if (event.isCancelled()) {
                return false;
            }
            speed = event.speed;
        }
        return instance.updateMovementInFluid(tag, speed);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F", ordinal = 0))
    private float callStepListener(final Entity instance) {
        if (mc.player == (Object) this) {
            final StepListener.StepEvent event = new StepListener.StepEvent(instance.getStepHeight());
            Vandalism.getInstance().getEventSystem().callExceptionally(StepListener.StepEvent.ID, event);
            return event.stepHeight;
        }
        return instance.getStepHeight();
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F", ordinal = 1))
    private float callStepListener2(final Entity instance) {
        if (mc.player == (Object) this) {
            final StepListener.StepEvent event = new StepListener.StepEvent(instance.getStepHeight());
            Vandalism.getInstance().getEventSystem().callExceptionally(StepListener.StepEvent.ID, event);
            return event.stepHeight;
        }
        return instance.getStepHeight();
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F", ordinal = 2))
    private float callStepListener3(final Entity instance) {
        if (mc.player == (Object) this) {
            final StepListener.StepEvent event = new StepListener.StepEvent(instance.getStepHeight());
            Vandalism.getInstance().getEventSystem().callExceptionally(StepListener.StepEvent.ID, event);
            return event.stepHeight;
        }
        return instance.getStepHeight();
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void callStepSuccessListener(final Vec3d movement, final CallbackInfoReturnable<Vec3d> cir) {
        if (mc.player == (Object) this) {
            final StepSuccessListener.StepSuccessEvent event = new StepSuccessListener.StepSuccessEvent(movement, cir.getReturnValue());
            Vandalism.getInstance().getEventSystem().callExceptionally(StepSuccessListener.StepSuccessEvent.ID, event);
            cir.setReturnValue(event.adjustMovementForCollisions);
        }
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d callStrafeListener(final Vec3d movementInput, final float speed, final float yaw) {
        if (mc.player == (Object) this) {
            final StrafeListener.StrafeEvent event = new StrafeListener.StrafeEvent(movementInput, speed, yaw, StrafeListener.Type.UPDATE_VELOCITY);
            Vandalism.getInstance().getEventSystem().callExceptionally(StrafeListener.StrafeEvent.ID, event);
            return movementInputToVelocity(event.movementInput, event.speed, event.yaw);
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void callEntityRemoveListener(Entity.RemovalReason reason, CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(EntityRemoveListener.EntityRemoveEvent.ID, new EntityRemoveListener.EntityRemoveEvent((Entity) (Object) this, reason));
    }

}
