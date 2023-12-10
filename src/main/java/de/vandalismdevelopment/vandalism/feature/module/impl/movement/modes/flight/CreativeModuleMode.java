package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.flight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.FlightModule;

public class CreativeModuleMode extends ModuleMulti<FlightModule> implements TickListener {

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
        if (this.mc.player == null) return;
        this.mc.player.getAbilities().flying = false;
        this.mc.player.getAbilities().allowFlying = this.mc.player.getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        this.mc.player.getAbilities().flying = true;
        this.mc.player.getAbilities().allowFlying = true;
    }

}
