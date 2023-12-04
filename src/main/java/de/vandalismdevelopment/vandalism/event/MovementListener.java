package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.util.math.Vec3d;

public interface MovementListener {

    default void onSprint(final SprintEvent event) {
    }

    class SprintEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 17;

        public boolean sprinting, bypass;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
            this.bypass = false;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onSprint(this);
        }

    }

    default void onMoveInput(final MoveInputEvent event) {
    }

    class MoveInputEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 18;

        public float movementForward, movementSideways;
        public final boolean slowDown;
        public final float slowDownFactor;

        public MoveInputEvent(final float movementForward, final float movementSideways, final boolean slowDown, final float slowDownFactor) {
            this.movementForward = movementForward;
            this.movementSideways = movementSideways;
            this.slowDown = slowDown;
            this.slowDownFactor = slowDownFactor;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onMoveInput(this);
        }

    }

    default void onStrafe(final StrafeEvent event) {
    }

    class StrafeEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 19;

        public Vec3d movementInput;
        public float speed, yaw;

        public StrafeEvent(final Vec3d movementInput, final float speed, final float yaw) {
            this.movementInput = movementInput;
            this.speed = speed;
            this.yaw = yaw;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onStrafe(this);
        }

    }

    default void onMoveFlying(final MoveFlyingEvent event) {
    }

    class MoveFlyingEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 21;

        public double sidewaysSpeed, upwardSpeed, forwardSpeed;

        public MoveFlyingEvent(final double sidewaysSpeed, final double upwardSpeed, final double forwardSpeed) {
            this.sidewaysSpeed = sidewaysSpeed;
            this.upwardSpeed = upwardSpeed;
            this.forwardSpeed = forwardSpeed;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onMoveFlying(this);
        }

    }

}
