package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.PacketListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VelocityModule extends Module implements PacketListener {

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
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (player() == null) return;
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket) {
            if (entityVelocityUpdateS2CPacket.getId() == player().getId()) {
                event.cancel();
            }
        }
    }

}
