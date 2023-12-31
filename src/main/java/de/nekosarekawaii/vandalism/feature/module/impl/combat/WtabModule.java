package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class WtabModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean movementTab, sprintTab;
    private long resetMS;
    private final MSTimer resetTime = new MSTimer();

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
        if (!(Math.random() * 100 < 80))
            return;

        if (!movementTab && mc.options.forwardKey.isPressed()) {
            mc.options.forwardKey.setPressed(false);
            mc.options.backKey.setPressed(true);
            movementTab = true;
        }

        if (!sprintTab && (mc.player.isSprinting() || mc.options.sprintKey.isPressed())) {
            mc.options.sprintKey.setPressed(false);
            sprintTab = true;
        }

        resetMS = RandomUtils.randomInt(100, 200);
        resetTime.reset();

    }

    @Override
    public void onTick() {
        //ChatUtil.chatMessage(movementTab + " " + sprintTab);

        if (sprintTab) {
            mc.options.sprintKey.setPressed(true);
            sprintTab = false;
        }

        if (!resetTime.hasReached(resetMS))
            return;

        if (movementTab) {
            mc.options.backKey.setPressed(false);
            mc.options.forwardKey.setPressed(true);
            movementTab = false;
        }
    }
}
