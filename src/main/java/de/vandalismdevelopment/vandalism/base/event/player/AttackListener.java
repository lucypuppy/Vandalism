package de.vandalismdevelopment.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.entity.Entity;

public interface AttackListener {

    void onAttackSend(final AttackSendEvent event);

    class AttackSendEvent extends AbstractEvent<AttackListener> {

        public static final int ID = 14;

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
