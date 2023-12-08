package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.entity.Entity;

public interface AttackListener {

    void onAttackSend(final AttackSendEvent event);

    class AttackSendEvent extends AbstractEvent<AttackListener> {

        public final static int ID = 24;

        public final Entity target;

        public AttackSendEvent(final Entity target) {
            this.target = target;
        }

        @Override
        public void call(final AttackListener listener) {
            listener.onAttackSend(this);
        }

    }

}
