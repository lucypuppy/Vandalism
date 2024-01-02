package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class AutoClickerModule extends AbstractModule implements TickGameListener {

    private final MSTimer clickTimer = new MSTimer();

    public AutoClickerModule() {
        super(
                "Auto Clicker",
                "Automatically uses the attack / block break key.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.clickTimer.hasReached((long) ((1000 / 10) * Math.random()), true)) {
            this.mc.doAttack();
        }
    }

}
