package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;

public class WTabModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean sprintTab;
    private Entity movementTarget;

    public WTabModule() {
        super(
                "W Tab",
                "Automatically tabs w when you are in combat which applies more velocity to your target.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                TickGameEvent.ID,
                AttackSendEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                TickGameEvent.ID,
                AttackSendEvent.ID
        );
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (!(Math.random() * 100 < 80) || this.mc.player == null) {
            return;
        }

        if (this.mc.options.forwardKey.isPressed() && this.movementTarget == null) {
            this.mc.options.forwardKey.setPressed(false);
            this.movementTarget = event.target;
        }

        if (!this.sprintTab && (this.mc.player.isSprinting() || this.mc.options.sprintKey.isPressed())) {
            this.mc.options.sprintKey.setPressed(false);
            this.sprintTab = true;
        }
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) {
            return;
        }

        if (sprintTab) {
            this.mc.options.sprintKey.setPressed(true);
            this.sprintTab = false;
        }

        if (this.movementTarget != null && this.mc.player.distanceTo(this.movementTarget) >= 3.1) {
            this.mc.options.forwardKey.setPressed(true);
            this.movementTarget = null;
        }
    }

}
