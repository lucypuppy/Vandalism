package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.modes.flight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.TickListener;
import de.foxglovedevelopment.foxglove.feature.impl.module.ModuleMode;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.FlightModule;

public class CreativeModuleMode extends ModuleMode<FlightModule> implements TickListener {

    public CreativeModuleMode(final FlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        if (player() == null) return;
        player().getAbilities().flying = false;
        player().getAbilities().allowFlying = player().getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (player() == null) return;
        player().getAbilities().flying = true;
        player().getAbilities().allowFlying = true;
    }

}
