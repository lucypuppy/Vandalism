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

package de.nekosarekawaii.vandalism.injection.mixins.fix.imgui;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.injection.access.IImGuiImplGlfw;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import imgui.flag.ImGuiMouseCursor;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ImGuiImplGlfw.class, remap = false)
public abstract class MixinImGuiImplGlfw implements IImGuiImplGlfw, MinecraftWrapper {

    @Shadow
    @Final
    private long[] mouseCursors;

    @Inject(method = "charCallback", at = @At(value = "INVOKE", target = "Limgui/ImGui;getIO()Limgui/ImGuiIO;", shift = At.Shift.BEFORE), cancellable = true)
    public void cancelCharCallback(final long windowId, final int c, final CallbackInfo ci) {
        if (!(mc.currentScreen instanceof ClientWindowScreen)) {
            ci.cancel();
        }
    }

    @Override
    public void vandalism$forceUpdateMouseCursor() {
        final long handle = mc.getWindow().getHandle();
        GLFW.glfwSetCursor(handle, this.mouseCursors[ImGuiMouseCursor.Arrow]);
        GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

}
