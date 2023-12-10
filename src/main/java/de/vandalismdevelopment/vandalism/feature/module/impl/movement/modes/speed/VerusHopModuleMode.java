package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.SpeedModule;
import de.vandalismdevelopment.vandalism.util.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class VerusHopModuleMode extends ModuleMulti<SpeedModule> implements MovementListener {

    public VerusHopModuleMode(final SpeedModule parent) {
        super("Verus Hop", parent);
    }

    private int offGroundTicks;
    private double moveSpeed;

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(MotionEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(MotionEvent.ID, this);
    }

    @Override
    public void onPreMotion(final MotionEvent event) {
        if ((this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) && this.mc.player.isOnGround())
            this.mc.player.jump();
    }

    @Override
    public void onPostMotion(final MotionEvent event) {
        if (this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) {
            if (this.mc.player.isOnGround()) {
                MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
                this.moveSpeed = MovementUtil.getBaseSpeed() * 2.4;
                this.offGroundTicks = 0;
            } else {
                if (this.offGroundTicks == 0)
                    this.moveSpeed += 0.01f;

                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, this.offGroundTicks <= 2 ? 0.0026f * 45 : 0.0026f);
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, (float) (Math.random() * 1E-5));
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.offGroundTicks++;
            }
        }
    }

}
