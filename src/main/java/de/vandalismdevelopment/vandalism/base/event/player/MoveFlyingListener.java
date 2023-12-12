package de.vandalismdevelopment.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MoveFlyingListener {

    void onMoveFlying(final MoveFlyingEvent event);

    class MoveFlyingEvent extends AbstractEvent<MoveFlyingListener> {

        public static final int ID = 17;

        public double sidewaysSpeed, upwardSpeed, forwardSpeed;

        public MoveFlyingEvent(final double sidewaysSpeed, final double upwardSpeed, final double forwardSpeed) {
            this.sidewaysSpeed = sidewaysSpeed;
            this.upwardSpeed = upwardSpeed;
            this.forwardSpeed = forwardSpeed;
        }

        @Override
        public void call(final MoveFlyingListener listener) {
            listener.onMoveFlying(this);
        }

    }

}
