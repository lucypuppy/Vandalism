package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.nofall;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaModuleMode extends ModuleMulti<NoFallModule> implements OutgoingPacketListener {

    public VanillaModuleMode(final NoFallModule parent) {
        super("Vanilla", parent);
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket && this.mc.player.fallDistance > 2.0f) {
            playerPacket.onGround = true;
        }
    }
}
