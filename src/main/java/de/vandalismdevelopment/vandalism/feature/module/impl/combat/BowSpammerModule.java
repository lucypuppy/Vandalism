package de.vandalismdevelopment.vandalism.feature.module.impl.combat;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.base.event.WorldListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public class BowSpammerModule extends AbstractModule implements TickListener {

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

    private final MSTimer shootTimer = new MSTimer();

    public BowSpammerModule() {
        super("Bow Spammer", "Lets you spam arrows with a bow.", Category.COMBAT, VersionRange.single(VersionEnum.r1_8));

        this.disableAfterSession();
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
        if (this.mc.player == null) {
            return;
        }

        if (this.mc.player.getMainHandStack().isOf(Items.BOW)) {
            if (this.shootTimer.hasReached(this.shootDelay.getValue(), true)) {
                for (int i = 0; i < this.maxPacketsPerTick.getValue(); i++) {
                    this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
                }
            }
        }
    }

}
