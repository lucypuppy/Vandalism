package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.EventPriorities;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

@ModuleInfo(name = "Mod Packet Blocker", description = "Blocks incoming and outgoing packets from mods.", category = FeatureCategory.MISC, isDefaultEnabled = true)
public class ModPacketBlockerModule extends Module implements PacketListener {

    public final Value<Boolean> fabric = new BooleanValue(
            "Block Fabric Packets",
            "Blocks packets from Fabric.",
            this,
            true
    );

    private final Value<Boolean> journeymap = new BooleanValue(
            "Block JourneyMap Packets",
            "Blocks packets from JourneyMap.",
            this,
            true
    ).visibleConsumer(() -> FabricLoader.getInstance().isModLoaded("journeymap"));

    private final Value<Boolean> roughlyEnoughItems = new BooleanValue(
            "Block Roughly Enough Items Packets",
            "Blocks packets from Roughly Enough Items.",
            this,
            true
    ).visibleConsumer(() -> FabricLoader.getInstance().isModLoaded("roughlyenoughitems"));

    private final Value<Boolean> architectury = new BooleanValue(
            "Block Architectury Packets",
            "Blocks packets from Architectury.",
            this,
            true
    ).visibleConsumer(() -> FabricLoader.getInstance().isModLoaded("architectury"));

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, EventPriorities.HIGH.getPriority());
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.isCancelled()) return;
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadC2SPacket) {
            final Identifier channel = customPayloadC2SPacket.getChannel();
            final String channelName = channel.getNamespace();
            switch (channelName) {
                case "journeymap" -> {
                    if (this.journeymap.getValue()) {
                        event.cancel();
                    }
                }
                case "roughlyenoughitems" -> {
                    if (this.roughlyEnoughItems.getValue()) {
                        event.cancel();
                    }
                }
                case "architectury" -> {
                    if (this.architectury.getValue()) {
                        event.cancel();
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void onRead(final PacketEvent event) {
        if (event.isCancelled()) return;
        if (event.packet instanceof final CustomPayloadS2CPacket customPayloadS2CPacket) {
            final Identifier channel = customPayloadS2CPacket.getChannel();
            final String channelName = channel.getNamespace();
            switch (channelName) {
                case "journeymap" -> {
                    if (this.journeymap.getValue()) {
                        event.cancel();
                    }
                }
                case "roughlyenoughitems" -> {
                    if (this.roughlyEnoughItems.getValue()) {
                        event.cancel();
                    }
                }
                case "architectury" -> {
                    if (this.architectury.getValue()) {
                        event.cancel();
                    }
                }
                default -> {
                }
            }
        }
    }

}
