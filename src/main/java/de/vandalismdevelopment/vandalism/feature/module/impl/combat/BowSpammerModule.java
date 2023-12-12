package de.vandalismdevelopment.vandalism.feature.module.impl.combat;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.game.TickGameListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public class BowSpammerModule extends AbstractModule implements TickGameListener {

    private final Value<Integer> maxPacketsPerTick = new IntegerValue(
            this,
            "Max Packets Per Tick",
            "The maximum amount of packets sent per tick.",
            10,
            5,
            100
    );

    private final Value<Integer> shootDelay = new IntegerValue(
            this,
            "Shoot Delay",
            "The delay between shots.",
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
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickGameEvent.ID, this);
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
