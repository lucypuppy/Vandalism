package de.nekosarekawaii.vandalism.feature.module.impl.development;

import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public class PacketManagerModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    private final ModeValue modeValue = new ModeValue(this, "Mode", "The mode of the packet manager.", "Cancel", "Log");

    public PacketManagerModule() {
        super("Packet Manager", "Allows to cancel or log packets.", Category.DEVELOPMENT);
        for (final NetworkState networkState : NetworkState.values()) {
            final String networkStateName = StringUtils.normalizeEnumName(networkState.name());
            final ValueGroup valueGroup = new ValueGroup(this, networkStateName, networkStateName + " Packets");
            final String[] serverPackets = networkState.getPacketIdToPacketMap(NetworkSide.CLIENTBOUND).values().stream().map(Class::getSimpleName).toArray(String[]::new);
            if (serverPackets.length > 0) {
                new MultiModeValue(
                        valueGroup,
                        "Server Packets",
                        "Incoming Packets",
                        serverPackets
                );
            }
            final String[] clientPackets = networkState.getPacketIdToPacketMap(NetworkSide.SERVERBOUND).values().stream().map(Class::getSimpleName).toArray(String[]::new);
            if (clientPackets.length > 0) {
                new MultiModeValue(
                        valueGroup,
                        "Client Packets",
                        "Outgoing Packets",
                        clientPackets
                );
            }
        }
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this, Priorities.HIGH);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    private boolean handlePacket(final boolean outgoing, final Packet<?> packet, final NetworkState networkState) {
        final String packetName = packet.getClass().getSimpleName();
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final ValueGroup valueGroup && valueGroup.getName().equalsIgnoreCase(networkState.name())) {
                for (final Value<?> valueGroupValue : valueGroup.getValues()) {
                    if (valueGroupValue instanceof final MultiModeValue multiModeValue) {
                        if (multiModeValue.getValue().contains(packetName)) {
                            if (this.modeValue.getSelectedIndex() == 1) {
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
                                break;
                            } else return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (this.handlePacket(false, event.packet, event.networkState)) {
            event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (this.handlePacket(true, event.packet, event.networkState)) {
            event.cancel();
        }
    }

}
