package de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class UpdatedNCPModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener {
    private int offGroundTicks = 0;
    private double moveSpeed = 0;

    public UpdatedNCPModuleMode() {
        super("Updated NCP");
    }

    private void reset() {
        this.offGroundTicks = 0;
        this.moveSpeed = 0;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (MovementUtil.isMoving() && this.mc.player.isOnGround()) {
            this.mc.player.jump();
        }
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.isOnGround()) {
            //MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
            this.moveSpeed = MovementUtil.getBaseSpeed() * 2.15;
            this.offGroundTicks = 0;
        } else {
            if (this.offGroundTicks == 0) {
              //  this.moveSpeed += 0.01f;
            }
            final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, this.offGroundTicks <= 2 ? 0.0026f * 45 : 0.0026f);
            final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector,12);
            this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
            this.offGroundTicks++;
        }
    }
}
