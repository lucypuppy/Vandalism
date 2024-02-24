/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.event.normal.player.RaytraceListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.event.normal.render.Render3DListener;
import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements IGameRenderer, MinecraftWrapper {

    @Unique
    private double vandalism$range = -1;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V", shift = At.Shift.AFTER))
    private void callRotationListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(RotationListener.RotationEvent.ID, new RotationListener.RotationEvent());
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double changeRange(final double constant) {
        if (vandalism$range != -1) {
            return vandalism$range;
        } else {
            final RaytraceListener.RaytraceEvent event = new RaytraceListener.RaytraceEvent(constant);
            Vandalism.getInstance().getEventSystem().postInternal(RaytraceListener.RaytraceEvent.ID, event);
            return event.range;
        }
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"))
    private boolean alwaysSurvival(final ClientPlayerInteractionManager instance) {
        return vandalism$range == -1 && instance.hasExtendedReach();
    }

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

    @Inject(method = "renderWorld", at = @At(value = "FIELD", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    private void callRender3DListener(final float tickDelta, final long limitTime, final MatrixStack matrices, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(
                Render3DListener.Render3DEvent.ID,
                new Render3DListener.Render3DEvent(tickDelta, limitTime, matrices)
        );
    }

}
