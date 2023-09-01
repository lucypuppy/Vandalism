package de.nekosarekawaii.foxglove.util;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BlockHitAnimations {

    // Slightly changed suicide blockhit animation (applySwingOffset in HeldItemRenderer)
    public static void suicide(final MatrixStack matrices, final float swingProgress) {
        final float swing = MathHelper.sin(MathHelper.sqrt(swingProgress / 33.0f) * (float) Math.PI);

        matrices.translate(0,-0.1,0.2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swing * -80.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(swing * -45.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(swing * -20.0F));
    }

}
