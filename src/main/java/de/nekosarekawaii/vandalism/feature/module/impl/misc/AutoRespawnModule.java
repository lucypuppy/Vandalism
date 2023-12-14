package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class AutoRespawnModule extends AbstractModule implements TickGameListener {

    private final BooleanValue instantRespawn = new BooleanValue(
            this,
            "Instant Respawn",
            "Instantly respawns you when you die.",
            false
    );

    private final IntegerValue delay = new IntegerValue(
            this,
            "Delay",
            "The delay in ticks before respawning.",
            2000,
            0,
            10000
    ).visibleCondition(() -> !this.instantRespawn.getValue());

    private final BooleanValue autoBack = new BooleanValue(
            this,
            "Auto Back",
            "Automatically uses the back command when you die.",
            false
    );

    private final MSTimer delayTimer = new MSTimer();

    public AutoRespawnModule() {
        super("Auto Respawn", "Automatically respawns you when you die.", Category.MISC);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.world == null || !this.mc.player.isDead()) return;

        // check if we should instantly respawn, if not check is the delay has passed
        if (!this.instantRespawn.getValue() && !this.delayTimer.hasReached(this.delay.getValue(), true)) {
            return;
        }

        // request respawn from server
        this.mc.player.requestRespawn();

        // automatically send /back if enabled
        if (this.autoBack.getValue()) this.mc.getNetworkHandler().sendChatCommand("back");
    }

}
