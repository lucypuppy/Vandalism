package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.SpeedModule;
import de.nekosarekawaii.foxglove.util.MovementUtil;

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
        if (player() == null) return;
        if (player().forwardSpeed != 0 || player().sidewaysSpeed != 0) {
            if (player().isOnGround()) player().jump();
            MovementUtil.setSpeed(1.5);
        }
    }

}
