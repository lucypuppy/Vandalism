package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.SpeedModule;
import de.vandalismdevelopment.vandalism.util.MovementUtil;

public class LongHopModuleMode extends ModuleMulti<SpeedModule> implements TickListener {

    public LongHopModuleMode(final SpeedModule parent) {
        super("Long Hop", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        if (this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) {
            if (this.mc.player.isOnGround()) this.mc.player.jump();
            MovementUtil.setSpeed(1.5);
        }
    }

}
