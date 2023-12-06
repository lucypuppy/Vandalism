package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.SpeedModule;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;

public class VerusHopModuleMode extends ModuleMode<SpeedModule> implements MovementListener {

    public VerusHopModuleMode(final SpeedModule parent) {
        super("Verus Hop", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(MotionEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(MotionEvent.ID, this);
    }

    @Override
    public void onPreMotion(MotionEvent event) {

        //this.lastSpeed = MovementUtil.getSpeed();

    }

    @Override
    public void onPostMotion(MotionEvent event) {
        if (this.player().forwardSpeed != 0 || this.player().sidewaysSpeed != 0) {
            if (this.player().isOnGround()) {
                this.player().jump();
                MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
            } else {
                MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 2.45);
                player().setVelocity(player().getVelocity().getX(), player().getVelocity().getY() - 0.01, player().getVelocity().getZ());
            }
        }
    }

}
