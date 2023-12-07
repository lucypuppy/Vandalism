package de.vandalismdevelopment.vandalism.util.minecraft.impl.player;

/**
 * A utility class for setting the timer speed.
 *
 * @author Lucy
 * @see de.vandalismdevelopment.vandalism.injection.mixins.feature.module.MixinRenderTickCounter
 */
public class TimerUtil {

    /**
     * The timer speed.
     *
     * @see #setSpeed(float)
     * @see #getSpeed()
     */
    private static float timerSpeed = 1.f;

    /**
     * Sets the timer speed.
     *
     * @param speed the timer speed
     */
    public static void setSpeed(float speed) {
        TimerUtil.timerSpeed = speed;
    }

    /**
     * Returns the timer speed. Wow!
     *
     * @return the timer speed
     */
    public static float getSpeed() {
        return timerSpeed;
    }

}
