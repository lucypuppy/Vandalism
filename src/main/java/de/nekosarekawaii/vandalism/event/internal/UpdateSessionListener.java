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
import net.minecraft.client.session.Session;

public interface UpdateSessionListener {

    void onUpdateSession(final UpdateSessionEvent event);

    class UpdateSessionEvent extends AbstractEvent<UpdateSessionListener> {

        public static final int ID = 12;

        public final Session oldSession;
        public final Session newSession;

        public UpdateSessionEvent(Session oldSession, Session newSession) {
            this.oldSession = oldSession;
            this.newSession = newSession;
        }

        @Override
        public void call(UpdateSessionListener listener) {
            listener.onUpdateSession(this);
        }

    }

}
