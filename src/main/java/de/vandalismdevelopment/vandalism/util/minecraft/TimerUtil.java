package de.vandalismdevelopment.vandalism.util.minecraft;

public class TimerUtil {

    private static float TIMER_SPEED = 1.0f;

    public static void setSpeed(final float speed) {
        TIMER_SPEED = Math.max(0.01f, speed);
    }

    public static float getSpeed() {
        return TIMER_SPEED;
    }

}
