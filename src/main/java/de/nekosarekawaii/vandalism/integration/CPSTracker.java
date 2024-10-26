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

package de.nekosarekawaii.vandalism.integration;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CPSTracker implements MouseInputListener, KeyboardInputListener, MinecraftWrapper {

    private final List<Long> leftClicks;
    private final List<Long> rightClicks;

    public CPSTracker() {
        this.leftClicks = new ArrayList<>();
        this.rightClicks = new ArrayList<>();

        Vandalism.getInstance().getEventSystem().subscribe(this, MouseEvent.ID, KeyboardInputEvent.ID);
    }

    public void update() {
        if (!this.leftClicks.isEmpty()) {
            this.leftClicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }
        if (!this.rightClicks.isEmpty()) {
            this.rightClicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }
    }

    public void leftClick() {
        this.leftClicks.add(Util.getMeasuringTimeMs());
    }

    public void rightClick() {
        this.rightClicks.add(Util.getMeasuringTimeMs());
    }

    public int getLeftClicks() {
        return this.leftClicks.size();
    }

    public int getRightClicks() {
        return this.rightClicks.size();
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        if (key == this.mc.options.attackKey.boundKey.getCode()) {
            this.leftClick();
        } else if (key == this.mc.options.useKey.boundKey.getCode()) {
            this.rightClick();
        }
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (event.action != GLFW.GLFW_PRESS) return;
        final int button = event.button;
        if (button == this.mc.options.attackKey.boundKey.getCode()) {
            this.leftClick();
        } else if (button == this.mc.options.useKey.boundKey.getCode()) {
            this.rightClick();
        }
    }

}
