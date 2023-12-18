package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.speed;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class VerusHopModuleMode extends ModuleMulti<SpeedModule> implements MotionListener {

    public VerusHopModuleMode(final SpeedModule parent) {
        super("Verus Hop", parent);
    }

    private int offGroundTicks;
    private double moveSpeed;

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(MotionEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(MotionEvent.ID, this);
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
