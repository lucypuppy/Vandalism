package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class AutoFishModule extends AbstractModule implements TickGameListener {

    public final IntegerValue throwDelayValue = new IntegerValue(this, "Throw Delay", "Here you can input the custom throw delay value.", 1000, 0, 5000);
    public final IntegerValue retractDelayValue = new IntegerValue(this, "Retract Delay", "Here you can input the custom retract delay value.", 500, 0, 1000);

    private final MSTimer retractDelayTimer = new MSTimer(), throwDelayTimer = new MSTimer();
    private boolean hasFish = false;

    public AutoFishModule() {
        super("Auto Fish", "Automatically fishes for you.", Category.MISC);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(TickGameEvent.ID, this);
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
