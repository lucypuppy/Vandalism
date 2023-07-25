package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface EntityPushListener {

    void onEntityPush(final EntityPushEvent entityPushEvent);

    class EntityPushEvent extends CancellableEvent<EntityPushListener> {

        public final static int ID = 8;

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
