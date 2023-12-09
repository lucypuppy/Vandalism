package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.nofall;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.NoFallModule;
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
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket
                && this.player().fallDistance > 2.0f)
            playerPacket.onGround = true;
    }

}
