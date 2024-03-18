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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.game.MouseInputListener;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse implements MinecraftWrapper {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void callMouseEvent_Button(final long window, final int button, final int action, final int mods, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            Vandalism.getInstance().getEventSystem().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(button, action, mods));
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void callMouseEvent_Scroll(final long window, final double horizontal, final double vertical, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            Vandalism.getInstance().getEventSystem().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(true, horizontal, vertical));
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void callMouseEvent_Pos(final long window, final double x, final double y, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            Vandalism.getInstance().getEventSystem().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(false, x, y));
        }
    }

}
