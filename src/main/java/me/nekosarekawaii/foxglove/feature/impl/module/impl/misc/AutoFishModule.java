package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@ModuleInfo(name = "Auto Fish", description = "Automatically fishes for you.", category = FeatureCategory.MISC)
public class AutoFishModule extends Module implements TickListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (mc.player == null)
            return;

        if (mc.player.fishHook != null && mc.player.fishHook.caughtFish && mc.player.fishHook.getVelocity().y < -0.2) {
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
        } else {
            mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0));
        }
    }

}
