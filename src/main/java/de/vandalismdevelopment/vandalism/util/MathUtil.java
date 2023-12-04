package de.vandalismdevelopment.vandalism.util;

public class MathUtil {
    public static float DEG_TO_RAD = 0.01745329238f;
    public static float[] POSSIBLE_MOVEMENTS = new float[]{-1F, 0.0F, 1F};
    public static float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }
}
