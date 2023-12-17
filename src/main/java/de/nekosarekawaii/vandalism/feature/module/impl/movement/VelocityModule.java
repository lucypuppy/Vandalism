package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VelocityModule extends AbstractModule implements IncomingPacketListener {

    public VelocityModule() {
        super("Velocity", "Modifies the server and the damage source velocity you take.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        //For some reason the player can be null when joining a server.
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && this.mc.player != null && velocityPacket.getId() == this.mc.player.getId()) {
            event.cancel();
        }
    }

}
