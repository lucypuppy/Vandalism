package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;

public class WtabModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean sprintTab;
    private Entity movementTarget;

    public WtabModule() {
        super(
                "W-Tab",
                "Automaticalls tabs w when you are in combat.",
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
    public void onAttackSend(AttackSendEvent event) {
        if (!(Math.random() * 100 < 80) || mc.player == null)
            return;

        if (mc.options.forwardKey.isPressed() && movementTarget == null) {
            mc.options.forwardKey.setPressed(false);
            movementTarget = event.target;
        }

        if (!sprintTab && (mc.player.isSprinting() || mc.options.sprintKey.isPressed())) {
            mc.options.sprintKey.setPressed(false);
            sprintTab = true;
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null)
            return;

        if (sprintTab) {
            mc.options.sprintKey.setPressed(true);
            sprintTab = false;
        }

        if (movementTarget != null && mc.player.distanceTo(movementTarget) >= 3.1) {
            mc.options.forwardKey.setPressed(true);
            movementTarget = null;
        }
    }
}
