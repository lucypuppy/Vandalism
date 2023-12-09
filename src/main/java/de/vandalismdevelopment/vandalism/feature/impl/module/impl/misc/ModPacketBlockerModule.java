package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModPacketBlockerModule extends Module implements PacketListener {

    public final Value<Boolean> unloadFabricAPICallbacks = new BooleanValue(
            "Unload Fabric API Callbacks",
            "Unloads Fabric API callbacks.",
            this,
            true
    );

    private final Map<String, Value<Boolean>> platformSettings = new HashMap<>();

    public ModPacketBlockerModule() {
        super(
                "Mod Packet Blocker",
                "Blocks various packets from mods which could be detected by a server.",
                FeatureCategory.MISC,
                false,
                true
        );
        for (final String modId : Arrays.asList("journeymap", "roughlyenoughitems", "architectury")) {
            this.platformSettings.put(
                    modId,
                    new BooleanValue(
                            "Block " + modId + " Packets",
                            "Blocks packets from " + modId + ".",
                            this,
                            true
                    ).visibleConsumer(() -> FabricLoader.getInstance().isModLoaded(modId))
            );
        }
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
        String channel;
        // This just shows how bad java is, we'll have to wait for Java 22 to improve this using destructuring.
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadPacket) {
            channel = customPayloadPacket.payload().id().getNamespace();
        } else if (event.packet instanceof final CustomPayloadS2CPacket customPayloadPacket) {
            channel = customPayloadPacket.payload().id().getNamespace();
        } else {
            return;
        }
        for (final Map.Entry<String, Value<Boolean>> entry : this.platformSettings.entrySet()) {
            if (entry.getValue().getValue() && channel.startsWith(entry.getKey())) {
                event.cancel();
                return;
            }
        }
    }

}
