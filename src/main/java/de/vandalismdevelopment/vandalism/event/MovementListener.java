package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.util.math.Vec3d;

public interface MovementListener {

    default void onSprint(final SprintEvent event) {
    }

    class SprintEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 17;

        public boolean sprinting;
        public boolean force;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
            this.force = false;
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

    default void onEntityPush(final EntityPushEvent event) {
    }

    class EntityPushEvent extends CancellableEvent<MovementListener> {

        public final static int ID = 8;

        public double value;

        public EntityPushEvent(final double value) {
            this.value = value;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onEntityPush(this);
        }

    }

    default void onFluidPush(final FluidPushEvent event) {
    }

    class FluidPushEvent extends CancellableEvent<MovementListener> {

        public final static int ID = 9;

        public double speed;

        public FluidPushEvent(final double speed) {
            this.speed = speed;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onFluidPush(this);
        }

    }

    default void onStep(final StepEvent event) {
    }

    class StepEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 10;

        public float stepHeight;

        public StepEvent(final float stepHeight) {
            this.stepHeight = stepHeight;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onStep(this);
        }

    }

    default void onStepSuccess(final StepSuccessEvent event) {
    }

    class StepSuccessEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 22;

        public final Vec3d movement;
        public Vec3d adjustMovementForCollisions;

        public StepSuccessEvent(final Vec3d movement, final Vec3d adjustMovementForCollisions) {
            this.movement = movement;
            this.adjustMovementForCollisions = adjustMovementForCollisions;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onStepSuccess(this);
        }

    }

    default void onPreMotion(final MotionEvent event) {
    }

    default void onPostMotion(final MotionEvent event) {
    }

    enum MotionEventState {
        PRE, POST
    }

    class MotionEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 23;

        private final MotionEventState state;

        public MotionEvent(final MotionEventState state) {
            this.state = state;
        }

        @Override
        public void call(final MovementListener listener) {
            if (this.state == MotionEventState.PRE) listener.onPreMotion(this);
            else listener.onPostMotion(this);

        }

    }

}
