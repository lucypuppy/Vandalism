package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class AutoFishModule extends Module implements TickListener {

    public final Value<Integer> throwDelayValue = new SliderIntegerValue("Throw Delay", "Here you can input the custom throw delay value.", this, 1000, 0, 5000);
    public final Value<Integer> retractDelayValue = new SliderIntegerValue("Retract Delay", "Here you can input the custom retract delay value.", this, 500, 0, 1000);

    private final MSTimer retractDelayTimer = new MSTimer(), throwDelayTimer = new MSTimer();
    private boolean hasFish = false;

    public AutoFishModule() {
        super("Auto Fish", "Automatically fishes for you.", FeatureCategory.MISC, false, false);
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
        if (this.player() == null || this.networkHandler() == null) return;
        final FishingBobberEntity fishHook = this.player().fishHook;
        if (fishHook != null) {
            if (!this.hasFish && fishHook.caughtFish && fishHook.getVelocity().y < -0.2) {
                this.hasFish = true;
                this.retractDelayTimer.reset();
            }

            if (this.hasFish && this.retractDelayTimer.hasReached(this.retractDelayValue.getValue(), true)) {
                this.networkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
                this.throwDelayTimer.reset();
            }
        } else if (this.throwDelayTimer.hasReached(this.throwDelayValue.getValue(), true)) {
            this.networkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
            this.hasFish = false;
        }
    }

}
