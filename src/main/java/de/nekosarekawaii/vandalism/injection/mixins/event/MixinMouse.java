/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.event.game.MouseDeltaListener;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.event.game.SmoothCameraRotationsListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse implements MinecraftWrapper {

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void callMouseButtonListener(final long window, final int button, final int action, final int mods, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            final MouseInputListener.MouseEvent mouseEvent = new MouseInputListener.MouseEvent(button, action, mods);
            Vandalism.getInstance().getEventSystem().callExceptionally(MouseInputListener.MouseEvent.ID, mouseEvent);
            if (mouseEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void callMouseScrollListener(final long window, final double horizontal, final double vertical, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            final MouseInputListener.MouseEvent event = new MouseInputListener.MouseEvent(true, horizontal, vertical);
            Vandalism.getInstance().getEventSystem().callExceptionally(MouseInputListener.MouseEvent.ID, event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void callMousePosListener(final long window, final double x, final double y, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            Vandalism.getInstance().getEventSystem().callExceptionally(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(false, x, y));
        }
    }


    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    public void callMouseDeltaListener(final CallbackInfo ci) {
        final MouseDeltaListener.MouseDeltaEvent event = new MouseDeltaListener.MouseDeltaEvent(this.cursorDeltaX, this.cursorDeltaY);
        Vandalism.getInstance().getEventSystem().callExceptionally(MouseDeltaListener.MouseDeltaEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        this.cursorDeltaX = event.cursorDeltaX;
        this.cursorDeltaY = event.cursorDeltaY;
    }

    @Redirect(method = "updateMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;smoothCameraEnabled:Z"))
    private boolean callSmoothCameraRotationsEvent(final GameOptions instance) {
        final SmoothCameraRotationsListener.SmoothCameraRotationsEvent event = new SmoothCameraRotationsListener.SmoothCameraRotationsEvent();
        event.smoothCamera = instance.smoothCameraEnabled;
        Vandalism.getInstance().getEventSystem().callExceptionally(SmoothCameraRotationsListener.SmoothCameraRotationsEvent.ID, event);
        return event.smoothCamera;
    }

}
