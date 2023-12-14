package de.nekosarekawaii.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface EntityPushListener {

    void onEntityPush(final EntityPushEvent event);

    class EntityPushEvent extends CancellableEvent<EntityPushListener> {

        public static final int ID = 0;

        public double value;

        public EntityPushEvent(final double value) {
            this.value = value;
        }

        @Override
        public void call(final EntityPushListener listener) {
            listener.onEntityPush(this);
        }

    }

}
