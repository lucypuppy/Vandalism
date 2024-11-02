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

package de.nekosarekawaii.vandalism.event.internal;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.nekosarekawaii.vandalism.feature.module.Module;

public interface ModuleToggleListener {

    void onModuleToggle(final ModuleToggleEvent event);

    class ModuleToggleEvent extends AbstractEvent<ModuleToggleListener> {

        public static final int ID = 10;

        public final Module module;
        public boolean active;

        public ModuleToggleEvent(final Module module, final boolean active) {
            this.module = module;
            this.active = active;
        }

        public boolean wasActive() {
            return !active;
        }

        @Override
        public void call(final ModuleToggleListener listener) {
            listener.onModuleToggle(this);
        }

    }

}
