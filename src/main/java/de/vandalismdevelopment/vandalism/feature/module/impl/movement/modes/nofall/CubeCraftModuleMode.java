package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.nofall;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.network.IncomingPacketListener;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.NoFallModule;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftConstants;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class CubeCraftModuleMode extends ModuleMulti<NoFallModule> implements IncomingPacketListener {

    public CubeCraftModuleMode(final NoFallModule parent) {
        super("Cubecraft Ground", parent);
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
    public void onIncomingPacket(final IncomingPacketEvent event) {
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
