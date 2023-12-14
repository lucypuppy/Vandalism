package de.nekosarekawaii.vandalism.integration.rotation;

import com.mojang.datafixers.util.Function4;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import net.minecraft.util.math.MathHelper;

public enum RotationGCD implements IName {

    NONE((rotation, lastRotation, multiplier, iterations) -> rotation),
    REAL((rotation, lastRotation, multiplier, iterations) -> {
        float yaw = rotation.getYaw(), pitch = rotation.getPitch(),
                lastYaw = lastRotation.getYaw(), lastPitch = lastRotation.getPitch(),
                deltaYaw = MathHelper.subtractAngles(lastYaw, yaw),
                deltaPitch = MathHelper.subtractAngles(lastPitch, pitch);

        yaw = lastYaw;
        pitch = lastPitch;

        final float gcdCursorDeltaX = deltaYaw / 0.15F,
                gcdCursorDeltaY = deltaPitch / 0.15F;

        final double cursorDeltaX = gcdCursorDeltaX / multiplier,
                cursorDeltaY = gcdCursorDeltaY / multiplier;

        double partialDeltaX = 0, partialDeltaY = 0;

        for (int i = 0; i < iterations; i++) {
            final double nextDeltaX = cursorDeltaX / iterations,
                    nextDeltaY = cursorDeltaY / iterations;

            final int currentDeltaX = (int) Math.round(nextDeltaX + partialDeltaX),
                    currentDeltaY = (int) Math.round(nextDeltaY + partialDeltaY);

            partialDeltaX += nextDeltaX - currentDeltaX;
            partialDeltaY += nextDeltaY - currentDeltaY;

            final double newCursorDeltaX = currentDeltaX * multiplier,
                    newCursorDeltaY = currentDeltaY * multiplier;

            yaw += (float) newCursorDeltaX * 0.15F;
            pitch += (float) newCursorDeltaY * 0.15F;
        }

        return new Rotation(yaw, pitch);
    });

    private final String name;
    private final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda;

    RotationGCD(final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda) {
        this.name = StringUtils.normalizeEnumName(this.name());
        this.lambda = lambda;
    }

    public Function4<Rotation, Rotation, Double, Integer, Rotation> getLambda() {
        return lambda;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
