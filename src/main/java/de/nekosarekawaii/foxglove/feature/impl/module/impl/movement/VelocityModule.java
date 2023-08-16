package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

@ModuleInfo(name = "Velocity", description = "Cancels the velocity which applies to you. (except push velocity)", category = FeatureCategory.MOVEMENT)
public class VelocityModule extends Module implements PacketListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacketRead(final PacketEvent event) {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        final Packet<?> packet = event.packet;
        if (packet instanceof final EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket) {
            if (entityVelocityUpdateS2CPacket.getId() == player.getId()) {
                event.cancel();
            }
        }
    }

}
