package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;

public class CreativeModuleMode extends ModuleMulti<FlightModule> implements TickGameListener {

    public CreativeModuleMode(final FlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickGameEvent.ID, this);
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
