package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.flight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.FlightModule;

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
        if (this.player() == null) return;
        this.player().getAbilities().flying = false;
        this.player().getAbilities().allowFlying = this.player().getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (this.player() == null) return;
        this.player().getAbilities().flying = true;
        this.player().getAbilities().allowFlying = true;
    }

}
