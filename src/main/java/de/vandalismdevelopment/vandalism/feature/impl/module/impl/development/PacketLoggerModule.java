package de.vandalismdevelopment.vandalism.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.MultiSelectionValue;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketLoggerModule extends Module implements PacketListener {

    public PacketLoggerModule() {
        super(
                "Packet Logger",
                "Logs packets and their data.",
                FeatureCategory.DEVELOPMENT,
                false,
                false
        );
        final List<String> serverPackets = new ArrayList<>(), clientPackets = new ArrayList<>();
        for (final NetworkState networkState : NetworkState.values()) {
            for (final Class<? extends Packet<?>> packetClass : networkState.getPacketIdToPacketMap(NetworkSide.SERVERBOUND).values()) {
                clientPackets.add(packetClass.getSimpleName());
            }
            for (final Class<? extends Packet<?>> packetClass : networkState.getPacketIdToPacketMap(NetworkSide.CLIENTBOUND).values()) {
                serverPackets.add(packetClass.getSimpleName());
            }
        }
        new MultiSelectionValue("Server Packets",
                "Incoming packets",
                this,
                serverPackets.toArray(new String[0])
        );
        new MultiSelectionValue("Client Packets",
                "Outgoing packets",
                this,
                clientPackets.toArray(new String[0])
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.LOW);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.packet;
        final Class<?> packetClass = packet.getClass();
        final String packetName = packetClass.getSimpleName();
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final MultiSelectionValue multiSelectionValue && multiSelectionValue.getValue().contains(packetName)) {
                final StringBuilder text = new StringBuilder();
                if (event.state == PacketEventState.SEND) {
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

}
