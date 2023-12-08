package de.vandalismdevelopment.vandalism.util.minecraft.impl;

public class TimerUtil {

    private static float TIMER_SPEED = 1.0f;

    public static void setSpeed(final float speed) {
        TIMER_SPEED = speed;
    }

    public static float getSpeed() {
        return TIMER_SPEED;
    }

}
