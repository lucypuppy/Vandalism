package de.vandalismdevelopment.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;

public class AutoRespawnModule extends AbstractModule implements TickListener {

    private final Value<Boolean> instantRespawn = new BooleanValue(
            this,
            "Instant Respawn",
            "Instantly respawns you when you die.",
            false
    );

    private final Value<Integer> delay = new IntegerValue(
            this,
            "Delay",
            "The delay in ticks before respawning.",
            2000,
            0,
            10000
    ).visibleCondition(() -> !this.instantRespawn.getValue());

    private final Value<Boolean> autoBack = new BooleanValue(
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
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
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
