package de.vandalismdevelopment.vandalism.feature.impl.module.impl.combat;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderIntegerValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class BowSpammerModule extends Module implements TickListener {

    private final Value<Integer> maxPacketsPerTick = new SliderIntegerValue(
            "Max Packets Per Tick",
            "The maximum amount of packets sent per tick.",
            this,
            10,
            5,
            100
    );

    private final Value<Integer> shootDelay = new SliderIntegerValue(
            "Shoot Delay",
            "The delay between shots.",
            this,
            100,
            0,
            2000
    );

    private final MSTimer shootTimer;

    public BowSpammerModule() {
        super(
                "Bow Spammer",
                "Lets you spam arrows with a bow.",
                FeatureCategory.COMBAT,
                false,
                false
        );
        this.shootTimer = new MSTimer();
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
        if (player() == null || world() == null || interactionManager() == null) {
            return;
        }
        final ItemStack mainHandStack = player().getMainHandStack();
        if (
                mainHandStack.isEmpty() ||
                        mainHandStack.getItem() == null ||
                        mainHandStack.getItem() != Items.BOW
        ) {
            return;
        }
        if (this.shootTimer.hasReached(this.shootDelay.getValue(), true)) {
            for (int i = 0; i < 10; i++) {
                interactionManager().interactItem(player(), Hand.MAIN_HAND);
            }
        }
    }

}
