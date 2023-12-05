package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.RandomUtils;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderDoubleValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.stream.Stream;

public class FOVFuckerModule extends Module implements TickListener {

    private final Value<Float> maxDistance = new SliderFloatValue(
            "Max Distance",
            "The maximum distance to find targets.",
            this,
            5,
            2,
            10
    );

    private final Value<Double> targetYPosOffset = new SliderDoubleValue(
            "Target Y Pos Offset",
            "The offset for the y position you will be teleported to.",
            this,
            0.2,
            -5,
            5
    );

    private final Value<Double> targetHPosOffset = new SliderDoubleValue(
            "Target H Pos Offset",
            "The offset for the horizontal position you will be teleported to.",
            this,
            0.5,
            -5,
            5
    );

    private final Value<Boolean> useYawFromTarget = new BooleanValue(
            "Use Yaw From Target",
            "Uses the yaw from the target.",
            this,
            true
    );

    private final Value<Boolean> usePitchFromTarget = new BooleanValue(
            "Use Pitch From Target",
            "Uses the pitch from the target.",
            this,
            true
    );

    private final Value<Boolean> useSneakFromTarget = new BooleanValue(
            "Use Sneak From Target",
            "Makes you sneak if the target is sneaking.",
            this,
            true
    );

    private final Value<Boolean> alwaysFOV = new BooleanValue(
            "Always FOV",
            "This will always teleport you into the fov of the target.",
            this,
            false
    );

    public FOVFuckerModule() {
        super(
                "FOV Fucker",
                "Teleports you into the nearest player to mess up his fov.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    private AbstractClientPlayerEntity target;

    @Override
    public void onEnable() {
        this.target = null;
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
        this.target = null;
    }


    @Override
    public void onTick() {
        if (this.world() == null || this.player() == null) return;
        if (this.target == null) {
            final Stream<AbstractClientPlayerEntity> players = this.world().getPlayers().stream();
            this.target = players.sorted(Comparator.comparingDouble(player -> this.player().distanceTo(player)))
                    .filter(player -> this.player() != player && this.player().distanceTo(player) <= this.maxDistance.getValue())
                    .findFirst().orElse(null);
            return;
        }
        if (this.target.isDead() || this.world().getEntityById(this.target.getId()) == null) {
            this.target = null;
            return;
        }
        final double direction = (Math.atan2(
                this.target.forwardSpeed,
                this.target.sidewaysSpeed
        ) / Math.PI * 180.0F + this.target.getYaw()) * Math.PI / 180.0F;
        if (this.useYawFromTarget.getValue()) {
            this.player().setYaw(this.target.getHeadYaw());
            this.player().setBodyYaw(this.target.getBodyYaw());
            this.player().setHeadYaw(this.target.getHeadYaw());
        }
        if (this.usePitchFromTarget.getValue()) {
            this.player().setPitch(this.target.getPitch());
        }
        if (this.useSneakFromTarget.getValue()) {
            this.options().sneakKey.setPressed(this.target.isSneaking());
        }
        double diffZ = (this.target.getZ() - this.target.prevZ);
        double diffX = (this.target.getX() - this.target.prevX);
        float targetPitch = this.target.getPitch();
        double targetEyePosY = Math.abs((targetPitch < 0) ? targetPitch / this.target.getEyePos().y : 0);
        if (targetEyePosY > 0) {
            targetEyePosY += RandomUtils.randomFloat(
                    -(float) (targetEyePosY * this.target.getStandingEyeHeight()),
                    (float) (targetEyePosY * this.target.getStandingEyeHeight())
            ) * 0.6;
        }
        if (!this.alwaysFOV.getValue()) {
            targetEyePosY = 0;
        }
        float strafe = 0, forward = 0;
        if (diffZ != 0 || diffX != 0) {
            float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX));
            float yawDiff = MathHelper.wrapDegrees(yaw - this.target.getYaw() - 90F);

            if (yawDiff >= -67.5F && yawDiff <= 67.5F) {
                ++forward;
            }
            if (yawDiff <= -112.5F || yawDiff >= 112.5) {
                --forward;
            }
            if (yawDiff >= 22.5F && yawDiff <= 157.5F) {
                --strafe;
            }
            if (yawDiff >= -157.5F && yawDiff <= -22.5F) {
                ++strafe;
            }
        }
        final double hOffset = this.targetHPosOffset.getValue();
        final double xOffset = Math.sin(direction) * hOffset;
        final double zOffset = Math.cos(direction) * hOffset;
        double x = this.target.getX(), z = this.target.getZ();
        if (!this.alwaysFOV.getValue()) {
            x += xOffset;
            z -= zOffset;
        } else {
            x -= xOffset;
            z += zOffset;
            if (forward > 0) {
                if (strafe != 0) {
                    diffX /= 1.2f;
                    diffZ /= 1.2f;
                }
                x += diffX * 11;
                z += diffZ * 11;
            } else {
                if (strafe != 0) {
                    diffX *= 3;
                    diffZ *= 3;
                }
                x += (diffX * 1.5);
                z += (diffZ * 1.5);
            }
        }
        this.player().setPos(
                x,
                this.target.getY() + this.targetYPosOffset.getValue() + targetEyePosY,
                z
        );
    }

}
