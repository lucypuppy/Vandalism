package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModPacketBlockerModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    public final BooleanValue unloadFabricAPICallbacks = new BooleanValue(
            this,
            "Unload Fabric API Callbacks",
            "Unloads Fabric API callbacks.",
            true
    );

    private final Map<String, BooleanValue> platformSettings = new HashMap<>();

    public ModPacketBlockerModule() {
        super("Mod Packet Blocker", "Blocks various packets from mods which could be detected by a server.", Category.MISC);
        enableDefault();
        for (final String modId : Arrays.asList("journeymap", "roughlyenoughitems", "architectury")) {
            this.platformSettings.put(
                    modId,
                    new BooleanValue(
                            this,
                            "Block " + modId + " Packets",
                            "Blocks packets from " + modId + ".",
                            true
                    ).visibleCondition(() -> FabricLoader.getInstance().isModLoaded(modId))
            );
        }
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(IncomingPacketEvent.ID, this, Priorities.HIGH);
        DietrichEvents2.global().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(IncomingPacketEvent.ID, this);
        DietrichEvents2.global().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    private boolean cancel(final String channel) {
        for (final Map.Entry<String, BooleanValue> entry : this.platformSettings.entrySet()) {
            if (entry.getValue().getValue() && channel.startsWith(entry.getKey())) return true;
        }
        return false;
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final CustomPayloadS2CPacket customPayloadPacket) {
            final String channel = customPayloadPacket.payload().id().getNamespace();
            if (this.cancel(channel)) event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadPacket) {
            final String channel = customPayloadPacket.payload().id().getNamespace();
            if (this.cancel(channel)) event.cancel();
        }
    }

}
