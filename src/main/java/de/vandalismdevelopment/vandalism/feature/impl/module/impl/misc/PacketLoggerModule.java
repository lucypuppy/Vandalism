package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import de.vandalismdevelopment.vandalism.util.inventory.ScreenHandlerTypes;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.Identifier;

public class PacketLoggerModule extends Module implements PacketListener {

    private final Value<Boolean> customPayloadPacket = new BooleanValue(
            "Custom Payload Packet",
            "Logs custom payload packets into the Chat.",
            this,
            false
    );

    private final Value<Boolean> requestCommandCompletionsC2SPacket = new BooleanValue(
            "Request Command Completions C2S Packet",
            "Logs request command completions packets into the Chat.",
            this,
            false
    );

    private final Value<Boolean> clickSlotC2SPacket = new BooleanValue(
            "Click Slot C2S Packet",
            "Logs click slot packets into the Chat.",
            this,
            false
    );

    private final Value<Boolean> openScreenS2CPacket = new BooleanValue(
            "Open Screen S2C Packet",
            "Logs open screen packets into the Chat.",
            this,
            false
    );

    public PacketLoggerModule() {
        super(
                "Packet Logger",
                "Logs game packets and their data into the chat.",
                FeatureCategory.MISC,
                true,
                false
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
        if (packet instanceof final CustomPayloadC2SPacket c2SPacket) {
            if (this.customPayloadPacket.getValue()) {
                final Identifier channel = c2SPacket.payload().id();
                final String channelName = channel.getNamespace(), channelPath = channel.getPath();
                ChatUtils.infoChatMessage("Outgoing custom payload packet > Channel Name: " + channelName + " | Channel Path: " + channelPath);
            }
        } else if (packet instanceof final RequestCommandCompletionsC2SPacket c2SPacket) {
            if (this.requestCommandCompletionsC2SPacket.getValue()) {
                ChatUtils.infoChatMessage(
                        "Outgoing request command completions packet > Id: " + c2SPacket.getCompletionId() + " | " +
                                "Command: \"" + c2SPacket.getPartialCommand() + "\""
                );
            }
        } else if (packet instanceof final ClickSlotC2SPacket c2SPacket) {
            if (this.clickSlotC2SPacket.getValue()) {
                final StringBuilder modifiedStacks = new StringBuilder();
                for (final ItemStack itemStack : c2SPacket.getModifiedStacks().values().toArray(new ItemStack[0])) {
                    modifiedStacks.append(itemStack.getName().getString()).append(';');
                }
                ChatUtils.infoChatMessage(
                        "Outgoing click slot packet > Sync Id: " + c2SPacket.getSyncId() + " | " +
                                "Revision: " + c2SPacket.getRevision() + " | " +
                                "Slot: " + c2SPacket.getSlot() + " | " +
                                "Button: " + c2SPacket.getButton() + " | " +
                                "Action Type: " + c2SPacket.getActionType().name() + " | " +
                                "Stack: " + c2SPacket.getStack().getName().getString() + " | " +
                                "Modified Stacks: " + modifiedStacks
                );
            }
        } else if (packet instanceof final CustomPayloadS2CPacket s2CPacket) {
            if (this.customPayloadPacket.getValue()) {
                final Identifier channel = s2CPacket.payload().id();
                final String channelName = channel.getNamespace(), channelPath = channel.getPath();
                ChatUtils.infoChatMessage(
                        "Incoming custom payload packet > Channel Name: " + channelName + " | " +
                                "Channel Path: " + channelPath
                );
            }
        } else if (packet instanceof final OpenScreenS2CPacket s2cPacket) {
            if (this.openScreenS2CPacket.getValue()) {
                ChatUtils.infoChatMessage(
                        "Incoming open screen packet > Name: " + s2cPacket.getName() + " | " +
                                "Sync Id: " + s2cPacket.getSyncId() + " | " +
                                "Screen Handler Type Id: " +
                                ScreenHandlerTypes.getId(s2cPacket.getScreenHandlerType())
                );
            }
        }
    }

}
