package de.vandalismdevelopment.vandalism.util.minecraft;

public class TimerHack {

    private static final float DEFAULT_TIMER_SPEED = 1.0F;

    private static float TIMER_SPEED = DEFAULT_TIMER_SPEED;

    public static void setSpeed(final float speed) {
        TIMER_SPEED = Math.max(0.01F, speed);
    }

    public static float getSpeed() {
        return TIMER_SPEED;
    }

    public static void reset() {
        TIMER_SPEED = DEFAULT_TIMER_SPEED;
    }

}
