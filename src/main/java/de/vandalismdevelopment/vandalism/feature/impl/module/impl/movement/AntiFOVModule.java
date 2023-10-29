package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderDoubleValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderFloatValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AntiFOVModule extends Module implements TickListener {

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
            0,
            -5,
            5
    );

    private final Value<Double> targetHPosOffset = new SliderDoubleValue(
            "Target H Pos Offset",
            "The offset for the horizontal position you will be teleported to.",
            this,
            2,
            -5,
            5
    );

    private final Value<Boolean> onlyPlayers = new BooleanValue(
            "Only Players",
            "Only teleport you behind players.",
            this,
            true
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

    private final Value<Boolean> alwaysFOV = new BooleanValue(
            "Always FOV",
            "This will always teleport you into the fov of the target.",
            this,
            false
    );

    public AntiFOVModule() {
        super(
                "AntiFOV",
                "Teleports you behind the nearest entity.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
    }

    //TODO: Add an option to be always centered to the camera if always fov is enabled.

    @Override
    public void onTick() {
        if (world() == null || player() == null) return;
        for (final Entity entity : world().getEntities()) {
            if (entity == player() || player().distanceTo(entity) > this.maxDistance.getValue()) continue;
            if (entity instanceof final LivingEntity target) {
                if (this.onlyPlayers.getValue() && !(target instanceof PlayerEntity)) continue;
                final double direction = (
                        Math.atan2(target.forwardSpeed, target.sidewaysSpeed) /
                                Math.PI * 180.0F + target.getYaw()
                ) * Math.PI / 180.0F;
                if (this.useYawFromTarget.getValue()) {
                    player().setYaw(target.getHeadYaw());
                }
                if (this.usePitchFromTarget.getValue()) {
                    player().setPitch(target.getPitch());
                }
                final double
                        hOffset = this.targetHPosOffset.getValue(),
                        xOffset = Math.sin(direction) * hOffset,
                        zOffset = Math.cos(direction) * hOffset;
                double x = entity.getX(), z = entity.getZ();
                if (!this.alwaysFOV.getValue()) {
                    x += xOffset;
                    z -= zOffset;
                } else {
                    x -= xOffset;
                    z += zOffset;
                }
                player().setPos(
                        x,
                        entity.getY() + this.targetYPosOffset.getValue(),
                        z
                );
                break;
            }
        }
    }

}
