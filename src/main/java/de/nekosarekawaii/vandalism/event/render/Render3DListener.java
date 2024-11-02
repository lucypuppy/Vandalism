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

package de.nekosarekawaii.vandalism.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.util.math.MatrixStack;

public interface Render3DListener {

    void onRender3D(final float tickDelta, final MatrixStack matrixStack);

    class Render3DEvent extends AbstractEvent<Render3DListener> {

        public static final int ID = 34;

        private final MatrixStack matrixStack;
        private final float tickDelta;

        public Render3DEvent(final float tickDelta, final MatrixStack matrixStack) {
            this.matrixStack = matrixStack;
            this.tickDelta = tickDelta;
        }

        @Override
        public void call(final Render3DListener listener) {
            listener.onRender3D(this.tickDelta, this.matrixStack);
        }

    }

}
