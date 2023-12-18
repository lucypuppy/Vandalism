package de.nekosarekawaii.vandalism.feature.module.impl.development;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketLoggerModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    public PacketLoggerModule() {
        super("Packet Logger", "Logs packets and their data.", Category.DEVELOPMENT);
        final List<String> serverPackets = new ArrayList<>(), clientPackets = new ArrayList<>();
        for (final NetworkState networkState : NetworkState.values()) {
            for (final Class<? extends Packet<?>> packetClass : networkState.getPacketIdToPacketMap(NetworkSide.SERVERBOUND).values()) {
                clientPackets.add(packetClass.getSimpleName());
            }
            for (final Class<? extends Packet<?>> packetClass : networkState.getPacketIdToPacketMap(NetworkSide.CLIENTBOUND).values()) {
                serverPackets.add(packetClass.getSimpleName());
            }
        }
        new MultiModeValue(
                this,
                "Server Packets",
                "Incoming packets",
                serverPackets.toArray(new String[0])
        );
        new MultiModeValue(
                this,
                "Client Packets",
                "Outgoing packets",
                clientPackets.toArray(new String[0])
        );
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this, Priorities.LOW);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    private void logPacket(final boolean outgoing, final Packet<?> packet) {
        final Class<?> packetClass = packet.getClass();
        final String packetName = packetClass.getSimpleName();
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final MultiModeValue multiModeValue && multiModeValue.getValue().contains(packetName)) {
                final StringBuilder text = new StringBuilder();
                if (outgoing) {
                    text.append("Outgoing packet: ");
                } else {
                    text.append("Incoming packet: ");
                }
                text.append(packetName);
                if (this.mc.inGameHud != null) {
                    ChatUtil.infoChatMessage(text.toString());
                } else {
                    Vandalism.getInstance().getLogger().info(text.toString());
                }
            }
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        logPacket(false, event.packet);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        logPacket(true, event.packet);
    }
}
