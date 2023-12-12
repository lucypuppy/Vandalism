package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.nofall;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.network.IncomingPacketListener;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.NoFallModule;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaModuleMode extends ModuleMulti<NoFallModule> implements IncomingPacketListener {

    public VanillaModuleMode(final NoFallModule parent) {
        super("Vanilla", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket
                && this.mc.player.fallDistance > 2.0f)
            playerPacket.onGround = true;
    }

}
