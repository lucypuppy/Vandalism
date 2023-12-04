package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.util.math.Vec3d;

public interface MovementListener {

    default void onSprint(final SprintEvent event) {
    }

    class SprintEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 17;

        public boolean sprinting;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
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

        public final Vec3d movementInput;
        public final float speed, yaw;
        public Vec3d velocity;

        public StrafeEvent(final Vec3d movementInput, final float speed, final float yaw, final Vec3d movementInputAsVelocity) {
            this.movementInput = movementInput;
            this.speed = speed;
            this.yaw = yaw;
            this.velocity = movementInputAsVelocity;
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

        public double velocityX, velocityY, velocityZ;

        public MoveFlyingEvent(final double velocityX, final double velocityY, final double velocityZ) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onMoveFlying(this);
        }

    }

}
