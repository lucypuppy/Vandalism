package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.timer.impl.ms.MsTimer;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(name = "Auto Fish", description = "Automatically fishes for you.", category = FeatureCategory.MISC)
public class AutoFishModule extends Module implements TickListener {

    private final MsTimer retractDelay = new MsTimer(), throwDelay = new MsTimer();
    private boolean hasFish = false;
    public final Value<Integer> throwDelayValue = new SliderIntegerValue("Throw Delay", "Here you can input the custom throw delay value.", this, 1000, 0, 5000);
    public final Value<Integer> retractDelayValue = new SliderIntegerValue("Retract Delay", "Here you can input the custom retract delay value.", this, 500, 0, 1000);

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
        final ClientPlayerEntity player = mc.player;
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (player == null || networkHandler == null)
            return;

        if (player.fishHook != null) {
            if (!this.hasFish && player.fishHook.caughtFish && player.fishHook.getVelocity().y < -0.2) {
                this.hasFish = true;
                this.retractDelay.reset();
            }

            if (this.hasFish && this.retractDelay.hasReached(this.retractDelayValue.getValue(), true)) {
                networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
                this.throwDelay.reset();
            }
        } else if (throwDelay.hasReached(throwDelayValue.getValue(), true)) {
            networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
            this.hasFish = false;
        }
    }

}
