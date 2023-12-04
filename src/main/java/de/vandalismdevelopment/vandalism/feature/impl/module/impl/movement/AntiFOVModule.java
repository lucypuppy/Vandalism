package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.RandomUtils;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.MathUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderDoubleValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AntiFOVModule extends Module implements TickListener {

    private final Value<Float> maxDistance = new SliderFloatValue("Max Distance", "The maximum distance to find targets.", this, 5, 2, 10);
    private final Value<Double> targetYPosOffset = new SliderDoubleValue("Target Y Pos Offset", "The offset for the y position you will be teleported to.", this, 0, -5, 5);
    private final Value<Double> targetHPosOffset = new SliderDoubleValue("Target H Pos Offset", "The offset for the horizontal position you will be teleported to.", this, 2, -5, 5);
    private final Value<Boolean> onlyPlayers = new BooleanValue("Only Players", "Only teleport you behind players.", this, true);
    private final Value<Boolean> useYawFromTarget = new BooleanValue("Use Yaw From Target", "Uses the yaw from the target.", this, true);
    private final Value<Boolean> usePitchFromTarget = new BooleanValue("Use Pitch From Target", "Uses the pitch from the target.", this, true);
    private final Value<Boolean> useSneakFromTarget = new BooleanValue("Use Sneak From Target", "Makes you sneak if the target is sneaking.", this, true);
    private final Value<Boolean> alwaysFOV = new BooleanValue("Always FOV", "This will always teleport you into the fov of the target.", this, false);

    public AntiFOVModule() {
        super("Anti FOV", "Teleports you behind the nearest entity.", FeatureCategory.MOVEMENT, false, false);
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
    //TODO: Add an option to switch between targets via. the numpad or something.

    @Override
    public void onTick() {
        if (this.world() == null || this.player() == null) return;
        for (final Entity entity : this.world().getEntities()) {
            if (entity == this.player() || this.player().distanceTo(entity) > this.maxDistance.getValue())
                continue;
            if (entity instanceof final LivingEntity target) {
                if (this.onlyPlayers.getValue() && !(target instanceof PlayerEntity)) continue;
                final double direction = (Math.atan2(target.forwardSpeed, target.sidewaysSpeed) / Math.PI * 180.0F + target.getYaw()) * Math.PI / 180.0F;
                if (this.useYawFromTarget.getValue()) {
                    this.player().setYaw(target.getHeadYaw());
                    this.player().setBodyYaw(target.getBodyYaw());
                    this.player().setHeadYaw(target.getHeadYaw());
                }
                if (this.usePitchFromTarget.getValue()) {
                    this.player().setPitch(target.getPitch());
                }
                if (this.useSneakFromTarget.getValue()) {
                    this.options().sneakKey.setPressed(target.isSneaking());
                }
                double diffZ = (entity.getZ() - entity.prevZ);
                double diffX = (entity.getX() - entity.prevX);
                float targetPitch = entity.getPitch();
                double nervNetY = Math.abs((targetPitch < 0) ? targetPitch / entity.getEyePos().y : 0);
                if(nervNetY > 0) {
                    double abfuckY = RandomUtils.randomFloat(- (float) (nervNetY * entity.getStandingEyeHeight()), (float) (nervNetY * entity.getStandingEyeHeight()));

                   // ChatUtil.chatMessage("" + (float) (nervNetY / (entity.getStandingEyeHeight() * 2f)));
                    //ChatUtil.chatMessage("wtf " + (float) (nervNetY / (entity.getStandingEyeHeight())));
                    nervNetY += abfuckY * 0.6;
                }
                if(!this.alwaysFOV.getValue()){
                    nervNetY = 0;
                }
                double motion = Math.hypot(diffX, diffZ);
                float strafe = 0;
                float forward = 0;
                if (diffZ != 0 || diffX != 0){
                    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX));
                    float yawDiff = MathUtil.wrapAngleTo180_float(yaw - entity.getYaw() - 90F);

                    if (yawDiff >= -67.5F && yawDiff <= 67.5F){
                        ++forward;
                    }
                    if (yawDiff <= -112.5F || yawDiff >= 112.5){
                        --forward;
                    }
                    if (yawDiff >= 22.5F && yawDiff <= 157.5F){
                        --strafe;
                    }
                    if (yawDiff >= -157.5F && yawDiff <= -22.5F){
                        ++strafe;
                    }
                }

                final double hOffset = this.targetHPosOffset.getValue(), xOffset = Math.sin(direction) * this.targetHPosOffset.getValue(), zOffset = Math.cos(direction) * this.targetHPosOffset.getValue();
                double x = entity.getX(), z = entity.getZ();
                if (!this.alwaysFOV.getValue()) {
                    x += xOffset;
                    z -= zOffset;
                } else {
                    x -= xOffset;
                    z += zOffset;
                    if(forward > 0){
                       // ChatUtil.chatMessage("stf");
                        if(strafe != 0){
                            diffX /= 1.2f;
                            diffZ /= 1.2f;
                        }
                        x += diffX * 11;
                        z += diffZ * 11;
                    }else{
                        if(strafe != 0){
                            diffX *= 3;
                            diffZ *= 3;
                        }
                        x += (diffX * 1.5);
                        z += (diffZ * 1.5);
                    }
                 //   x += (entity.getX() - entity.prevX) * 10;
                 //   z += (entity.getZ() - entity.prevZ) * 10;
                }
                this.player().setPos(x, entity.getY() + this.targetYPosOffset.getValue() + nervNetY, z);
                break;
            }
        }
    }

}
