package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;

public class CreativeModuleMode extends ModuleMulti<FlightModule> implements TickGameListener {

    public CreativeModuleMode(final FlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(TickGameEvent.ID, this);
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
