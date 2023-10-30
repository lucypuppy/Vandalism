package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderIntegerValue;

public class AutoRespawnModule extends Module implements TickListener {

    private final Value<Boolean> instantRespawn = new BooleanValue(
            "Instant Respawn",
            "Instantly respawns you when you die.",
            this,
            false
    );

    private final Value<Integer> delay = new SliderIntegerValue(
            "Delay",
            "The delay in ticks before respawning.",
            this,
            2000,
            0,
            10000
    ).visibleConsumer(() -> !this.instantRespawn.getValue());

    private final Value<Boolean> autoBack = new BooleanValue(
            "Auto Back",
            "Automatically uses the back command when you die.",
            this,
            true
    );

    private final MSTimer delayTimer = new MSTimer();

    public AutoRespawnModule() {
        super(
                "Auto Respawn",
                "Automatically respawns you when you die.",
                FeatureCategory.MISC,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (world() == null || !player().isDead()) return;
        if (!this.instantRespawn.getValue() && !this.delayTimer.hasReached(this.delay.getValue(), true)) {
            return;
        }
        player().requestRespawn();
        if (this.autoBack.getValue()) {
            networkHandler().sendChatCommand("back");
        }
    }

}
