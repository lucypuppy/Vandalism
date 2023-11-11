package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

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

    public ModPacketBlockerModule() {
        super(
                "Mod Packet Blocker",
                "Blocks various packets from mods which could be detected by a server.",
                FeatureCategory.MISC,
                false,
                true
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }


    @Override
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof final CustomPayloadC2SPacket customPayloadC2SPacket) {
            final Identifier channel = customPayloadC2SPacket.payload().id();
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
        } else if (packet instanceof final CustomPayloadS2CPacket customPayloadS2CPacket) {
            final Identifier channel = customPayloadS2CPacket.payload().id();
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
