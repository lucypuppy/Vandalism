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

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.render.Camera;

public interface CameraOverrideListener {

    void onCameraOverride(final CameraOverrideEvent event);

    class CameraOverrideEvent extends CancellableEvent<CameraOverrideListener> {

        public static final int ID = 44;

        public final Camera camera;
        public final float tickDelta;

        public CameraOverrideEvent(final Camera camera, final float tickDelta) {
            this.camera = camera;
            this.tickDelta = tickDelta;
        }

        @Override
        public void call(final CameraOverrideListener listener) {
            listener.onCameraOverride(this);
        }

    }

}
