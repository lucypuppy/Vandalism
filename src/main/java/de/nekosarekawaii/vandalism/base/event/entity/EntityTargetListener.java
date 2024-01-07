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

package de.nekosarekawaii.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.entity.Entity;

public interface EntityTargetListener {

    void onEntityTarget(final EntityTargetEvent event);

    class EntityTargetEvent extends CancellableEvent<EntityTargetListener> {

        public static final int ID = 34;

        public Entity entity;
        public boolean isTarget = true;

        public EntityTargetEvent(final Entity entity) {
            this.entity = entity;
        }

        @Override
        public void call(final EntityTargetListener listener) {
            listener.onEntityTarget(this);
        }

    }

}
