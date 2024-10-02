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

package de.nekosarekawaii.vandalism.event.internal;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.entity.Entity;

public interface TargetListener {

    void onTarget(final TargetEvent event);

    class TargetEvent extends AbstractEvent<TargetListener> {

        public static final int ID = 11;

        public Entity entity;
        public boolean isTarget = true;
        public final boolean ignoreFriends;

        public TargetEvent(final Entity entity, final boolean ignoreFriends) {
            this.entity = entity;
            this.ignoreFriends = ignoreFriends;
        }

        @Override
        public void call(final TargetListener listener) {
            listener.onTarget(this);
        }

    }

}
