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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.cancellable.render.ScreenListener;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;

/**
 * Simple framebuffer wrapper that automatically resizes itself.
 * Made for easier usage.
 */
public class FramebufferWrapper extends SimpleFramebuffer implements ScreenListener, MinecraftWrapper {

    public FramebufferWrapper() {
        super(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        Vandalism.getInstance().getEventSystem().subscribe(ScreenEvent.ID, this);
        setClearColor(0, 0, 0, 0);
    }

    @Override
    public void onResizeScreen(ScreenEvent event) {
        this.resize(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
    }

}