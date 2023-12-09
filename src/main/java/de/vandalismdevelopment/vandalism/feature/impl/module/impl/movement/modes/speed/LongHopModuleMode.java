package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.SpeedModule;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;

public class LongHopModuleMode extends ModuleMode<SpeedModule> implements TickListener {

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
        if (this.player() == null) return;
        if (this.player().forwardSpeed != 0 || this.player().sidewaysSpeed != 0) {
            if (this.player().isOnGround()) this.player().jump();
            MovementUtil.setSpeed(1.5);
        }
    }

}
