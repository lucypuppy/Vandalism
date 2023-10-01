package de.vandalismdevelopment.vandalism.util.rotation;

import com.mojang.datafixers.util.Function4;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.util.rotation.rotationtypes.Rotation;
import net.minecraft.util.math.MathHelper;

public enum RotationGCD implements IName {

    NONE("None", (rotation, lastRotation, multiplier, iterations) -> rotation),


    //Credits to https://github.com/DietrichPaul/Clientbase/tree/master
    REAL("Real", (rotation, lastRotation, multiplier, iterations) -> {
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
            final double sollDeltaX = cursorDeltaX / iterations,
                    sollDeltaY = cursorDeltaY / iterations;

            final int istDeltaX = (int) Math.round(sollDeltaX + partialDeltaX),
                    istDeltaY = (int) Math.round(sollDeltaY + partialDeltaY);

            partialDeltaX += sollDeltaX - istDeltaX;
            partialDeltaY += sollDeltaY - istDeltaY;

            final double newCursorDeltaX = istDeltaX * multiplier,
                    newCursorDeltaY = istDeltaY * multiplier;

            yaw += (float) newCursorDeltaX * 0.15F;
            pitch += (float) newCursorDeltaY * 0.15F;
        }

        return new Rotation(yaw, pitch);
    });

    private final String name;
    private final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda;

    RotationGCD(final String name, final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda) {
        this.name = name;
        this.lambda = lambda;
    }

    public Function4<Rotation, Rotation, Double, Integer, Rotation> getLambda() {
        return lambda;
    }

    @Override
    public String getName() {
        return name;
    }

}
