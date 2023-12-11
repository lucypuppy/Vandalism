package de.vandalismdevelopment.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class AutoFishModule extends AbstractModule implements TickListener {

    public final Value<Integer> throwDelayValue = new IntegerValue("Throw Delay", "Here you can input the custom throw delay value.", this, 1000, 0, 5000);
    public final Value<Integer> retractDelayValue = new IntegerValue("Retract Delay", "Here you can input the custom retract delay value.", this, 500, 0, 1000);

    private final MSTimer retractDelayTimer = new MSTimer(), throwDelayTimer = new MSTimer();
    private boolean hasFish = false;

    public AutoFishModule() {
        super("Auto Fish", "Automatically fishes for you.", FeatureCategory.MISC, false, false);
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
        if (this.mc.player == null || this.mc.getNetworkHandler() == null) return;
        final FishingBobberEntity fishHook = this.mc.player.fishHook;
        if (fishHook != null) {
            if (!this.hasFish && fishHook.caughtFish && fishHook.getVelocity().y < -0.2) {
                this.hasFish = true;
                this.retractDelayTimer.reset();
            }

            if (this.hasFish && this.retractDelayTimer.hasReached(this.retractDelayValue.getValue(), true)) {
                this.mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
                this.throwDelayTimer.reset();
            }
        } else if (this.throwDelayTimer.hasReached(this.throwDelayValue.getValue(), true)) {
            this.mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
            this.hasFish = false;
        }
    }

}
