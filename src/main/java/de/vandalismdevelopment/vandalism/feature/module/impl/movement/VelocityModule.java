package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VelocityModule extends AbstractModule implements PacketListener {

    public VelocityModule() {
        super(
                "Velocity",
                "Modifies the server and the damage source velocity you take.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
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
        if (this.mc.player == null) return;
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket
                && velocityPacket.getId() == this.mc.player.getId())
            event.cancel();
    }

}
