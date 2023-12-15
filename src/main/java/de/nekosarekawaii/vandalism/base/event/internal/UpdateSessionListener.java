package de.nekosarekawaii.vandalism.base.event.internal;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.session.Session;

public interface UpdateSessionListener {

    void onUpdateSession(final UpdateSessionEvent event);

    class UpdateSessionEvent extends AbstractEvent<UpdateSessionListener> {

        public static final int ID = 29;

        public final Session oldSession;
        public Session newSession;

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
