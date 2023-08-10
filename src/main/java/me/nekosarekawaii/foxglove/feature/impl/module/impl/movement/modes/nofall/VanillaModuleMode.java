package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.PacketListener;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.NoFallModule;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaModuleMode extends ModuleMode<NoFallModule> implements PacketListener {

    public VanillaModuleMode(final NoFallModule parent) {
        super("Vanilla", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacketWrite(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket) {
            if (mc.player.fallDistance > 2.0f) {
                playerPacket.onGround = true;
            }
        }
    }

}
