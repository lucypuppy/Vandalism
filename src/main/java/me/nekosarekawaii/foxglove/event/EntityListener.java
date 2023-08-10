package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface EntityListener {

    default void onEntityPush(final EntityPushEvent entityPushEvent) {
    }

    class EntityPushEvent extends CancellableEvent<EntityListener> {

        public final static int ID = 8;

        public double value;

        public EntityPushEvent(final double value) {
            this.value = value;
        }

        @Override
        public void call(final EntityListener listener) {
            listener.onEntityPush(this);
        }

    }

}
