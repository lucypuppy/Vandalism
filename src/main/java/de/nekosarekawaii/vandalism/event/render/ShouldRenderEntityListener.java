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

package de.nekosarekawaii.vandalism.event.render;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;

public interface ShouldRenderEntityListener {

    void onShouldRenderEntity(final ShouldRenderEntityEvent event);

    class ShouldRenderEntityEvent extends CancellableEvent<ShouldRenderEntityListener> {

        public static final int ID = 49;

        public final Entity entity;
        public final Frustum frustum;
        public final double x;
        public final double y;
        public final double z;

        public ShouldRenderEntityEvent(final Entity entity, final Frustum frustum, final double x, final double y, final double z) {
            this.entity = entity;
            this.frustum = frustum;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void call(final ShouldRenderEntityListener listener) {
            listener.onShouldRenderEntity(this);
        }

    }

}