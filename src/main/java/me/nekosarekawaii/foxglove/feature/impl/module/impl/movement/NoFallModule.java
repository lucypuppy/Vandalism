package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(name = "No Fall", description = "Reduces or cancels your fall damage.", category = FeatureCategory.MOVEMENT)
public class NoFallModule extends Module implements PacketListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket) {
            if (mc.player.fallDistance > 2.0f) {
                playerPacket.onGround = true;
            }
        }
    }
}
