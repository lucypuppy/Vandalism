package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil extends MinecraftUtil {

    private final static float DEG_TO_RAD = 0.01745329238f;

    private final static float[] POSSIBLE_MOVEMENTS = new float[]{-1F, 0.0F, 1F};

    public static double getDirection() {
        return getDirection(0);
    }

    public static double getDirection(final float directionOffset) {
        if (player() == null) return 0;
        final float offset = (180.0F + directionOffset);
        return (Math.atan2(player().forwardSpeed, player().sidewaysSpeed) / Math.PI * offset + player().getYaw()) * Math.PI / offset;
    }

    public static Vec3d setSpeed(final double speed) {
        return setSpeed(speed, 0);
    }

    public static Vec3d setSpeed(final double speed, final float offset) {
        if (player() == null) return null;
        final double direction = getDirection(offset);
        player().setVelocity(Math.cos(direction) * speed, player().getVelocity().getY(), Math.sin(direction) * speed);
        return player().getVelocity();
    }

    //LivingEntity, check everytime after update
    public static Vec3d applyFriction(final Vec3d velocity, final float percentage) {
        if (player() == null) return Vec3d.ZERO;
        final BlockPos blockPos = player().getVelocityAffectingPos();
        final float p = world().getBlockState(blockPos).getBlock().getSlipperiness();
        float baseValue = 0.91F;
        float percentageFactor = percentage / 100f;
        float f = player().isOnGround()
                ? 1.0F - percentageFactor * (1.0F - p)
                : 1.0F - percentageFactor * (1.0F - baseValue);
        return velocity.multiply(f, 1, f);
    }

    public static double getBaseSpeed() {
        if (player() == null) return 0;
        double baseSpeed = 0.153D;
        if (!player().isSprinting()) {
            baseSpeed = 0.118D;
        }
        if (player().hasStatusEffect(StatusEffects.SPEED)) {
            final int amplifier = 1 + player().getStatusEffect(StatusEffects.SPEED).getAmplifier();
            final double fixedSpeedMotion = 0.11D;
            baseSpeed *= 1.0D + fixedSpeedMotion * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getSpeed() {
        if (player() == null) return 0;
        return Math.hypot(player().getVelocity().getX(), player().getVelocity().getZ());
    }

    public static void clip(final double vertical, final double horizontal) {
        if (player() == null) return;
        final double direction = getDirection();
        player().setPos(player().getX() - Math.sin(direction) * horizontal, player().getY() + vertical, player().getZ() + Math.cos(direction) * horizontal);
    }

    public static float[] getFixedMoveInputs(final float yaw) {
        final float[] INPUTS = new float[2];
        if (Math.abs(player().forwardSpeed) > 0F || Math.abs(player().sidewaysSpeed) > 0F) {
            final float wantedYaw = getInputAngle(player().getYaw());
            Vec3d movementInput;
            final float currentDX = MathHelper.sin(yaw * DEG_TO_RAD);
            final float currentDZ = MathHelper.cos(yaw * DEG_TO_RAD);
            // .. as seen here
            float currentBestForward = 1.0F;
            float currentBestStrafing = 0.0F;
            // the current best difference between any found combination and wantedYaw,
            // initialized to be 180 because that's the maximum value a difference can
            // be after being passed through MathHelper::wrapAngleToA18_float
            float currentBestDiff = Float.MAX_VALUE;
            // use this and not just hardcode 0.98F, because moveForward and moveStrafing
            // is also dependent on whether the player is sneaking or using an item
            float mag = Math.max(Math.abs(player().forwardSpeed), Math.abs(player().sidewaysSpeed));
            // loop through all possible combinations of player.moveForward and player.moveStrafing
            for (float forward : POSSIBLE_MOVEMENTS) {
                for (float strafing : POSSIBLE_MOVEMENTS) {
                    // don't do anything when the combination would make the player stand still
                    // (this would fuck up sin and cos)
                    if (forward == 0.0F && strafing == 0.0F) continue;
                    movementInput = new Vec3d(forward, player().upwardSpeed, strafing);
                    // Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
                    // motionX and motionZ the player would have with
                    // the current combination of moveForward and moveStrafing
                    //vec3d.x * (double)g - vec3d.z * (double)f
                    final float mX = (float) (movementInput.x * currentDZ - movementInput.z * currentDX);
                    //vec3d.z * (double)g + vec3d.x * (double)f
                    final float mZ = (float) (movementInput.z * currentDZ + movementInput.x * currentDX);
                    // the yaw angle relative to the players rotation this
                    // motion would make the player walk towards
                    //   mc.thePlayer.addChatMessage(new ChatComponentText("" + (180.0 / Math.PI)));
                    final float angle = (float) (Math.atan2(mZ, mX) * 180.0D / Math.PI - 90.0F);
                    // ... and the difference of it to the wanted yaw relative to the player
                    final float diff = Math.abs(MathHelper.wrapDegrees(angle - wantedYaw));
                    // set combination, if signed distance between
                    // wantedYaw and angle is lower then the last best difference
                    if (diff < currentBestDiff) {
                        currentBestForward = forward;
                        currentBestStrafing = strafing;
                        currentBestDiff = diff;
                    }
                }
            }
            INPUTS[0] = (currentBestForward * mag);
            INPUTS[1] = (currentBestStrafing * mag);
        }
        return INPUTS;
    }

    public static float getInputAngle(final float yaw) {
        final float vertical = player().forwardSpeed;
        final float horizontal = player().sidewaysSpeed;
        if (vertical > 0) {
            if (horizontal > 0) return yaw - 45F;
            else if (horizontal < 0) return yaw + 45F;
            else return yaw;
        } else if (vertical < 0) {
            if (horizontal > 0) return yaw - 135F;
            else if (horizontal < 0) return yaw + 135F;
            else return MathHelper.wrapDegrees(yaw - 180f);
        }
        if (horizontal > 0) return yaw - 90f;
        else if (horizontal < 0) return yaw + 90f;
        return yaw;
    }

}
