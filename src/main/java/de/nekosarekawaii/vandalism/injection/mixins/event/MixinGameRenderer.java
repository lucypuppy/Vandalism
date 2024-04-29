/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import com.llamalad7.mixinextras.sugar.Local;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.event.normal.render.Render3DListener;
import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements IGameRenderer, MinecraftWrapper {

    @Unique
    private double vandalism$range = -1;


    // i assume this is correct - Lucy
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void callRotationListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(RotationListener.RotationEvent.ID, new RotationListener.RotationEvent());
    }

    // TODO: Fix
    /*
    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double changeRange(final double constant) {
        if (vandalism$isSelfInflicted()) {
            return vandalism$range;
        } else {
            final RaytraceListener.RaytraceEvent event = new RaytraceListener.RaytraceEvent(constant);
            Vandalism.getInstance().getEventSystem().postInternal(RaytraceListener.RaytraceEvent.ID, event);
            return event.range;
        }
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"))
    private boolean alwaysSurvival(final ClientPlayerInteractionManager instance) {
        if (vandalism$isSelfInflicted()) {
            return false;
        }

        return instance.hasExtendedReach();
    }
*/

    @Override
    public boolean vandalism$isSelfInflicted() {
        return vandalism$range != -1;
    }

    @Override
    public double vandalism$getRange() {
        return vandalism$range;
    }

    @Override
    public void vandalism$setRange(final double range) {
        vandalism$range = range;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V"))
    private void callRender3DListener(float tickDelta, long limitTime, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2) {
        MatrixStack matrices = new MatrixStack();
        matrices.multiplyPositionMatrix(matrix4f2);
        Vandalism.getInstance().getEventSystem().postInternal(
                Render3DListener.Render3DEvent.ID,
                new Render3DListener.Render3DEvent(tickDelta, limitTime, matrices)
        );
    }

}
