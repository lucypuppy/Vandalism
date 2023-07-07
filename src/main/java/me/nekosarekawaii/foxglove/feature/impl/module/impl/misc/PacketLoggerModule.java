package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.EventPriorities;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

@ModuleInfo(name = "Packet Logger", description = "Logs incoming and outgoing packets into the Chat.", category = FeatureCategory.MISC)
public class PacketLoggerModule extends Module implements PacketListener {

    private final Value<Boolean> serverToClient = new BooleanValue("S2C", "Enable / Disable the logging of incoming packets.", this, true);

    private final Value<Boolean> customPayloadPacket = new BooleanValue("Custom Payload Packet", "Logs custom payload packets into the Chat.", this, false);

    private final Value<Boolean> requestCommandCompletionsPacket = new BooleanValue("Request Command Completions Packet", "Logs request command completions packet into the Chat.", this, false);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, EventPriorities.LOW.getPriority());
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.isCancelled()) return;
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadC2SPacket) {
            if (this.customPayloadPacket.getValue()) {
                final Identifier channel = customPayloadC2SPacket.getChannel();
                final String channelName = channel.getNamespace(), channelPath = channel.getPath();
                ChatUtils.infoChatMessage("Outgoing custom payload packet > Channel Name: " + channelName + " | Channel Path: " + channelPath);
            }
        } else if (event.packet instanceof final RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket) {
            if (this.requestCommandCompletionsPacket.getValue()) {
                ChatUtils.infoChatMessage("Outgoing request command completions packet > ID: " + requestCommandCompletionsC2SPacket.getCompletionId() + " | Command: \"" + requestCommandCompletionsC2SPacket.getPartialCommand() + "\"");
            }
        }
    }

    @Override
    public void onRead(final PacketEvent event) {
        if (event.isCancelled()) return;
        if (!this.serverToClient.getValue()) return;
        if (event.packet instanceof final CustomPayloadS2CPacket customPayloadS2CPacket) {
            if (this.customPayloadPacket.getValue()) {
                final Identifier channel = customPayloadS2CPacket.getChannel();
                final String channelName = channel.getNamespace(), channelPath = channel.getPath();
                ChatUtils.infoChatMessage("Incoming custom payload packet > Channel Name: " + channelName + " | Channel Path: " + channelPath);
            }
        }
    }

}
