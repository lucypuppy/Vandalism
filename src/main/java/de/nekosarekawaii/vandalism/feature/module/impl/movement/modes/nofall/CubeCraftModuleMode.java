package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.nofall;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.minecraft.MinecraftConstants;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class CubeCraftModuleMode extends ModuleMulti<NoFallModule> implements OutgoingPacketListener {

    public CubeCraftModuleMode(final NoFallModule parent) {
        super("Cubecraft Ground", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket
                && this.mc.player.fallDistance > 3f) {
            double next = this.mc.player.getY() % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR;
            next -= (int) this.mc.player.getY();
            next %= MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR;
            double y = this.mc.player.getY() - next;
            playerPacket.y = y;
            playerPacket.onGround = true;
        }
    }

}
