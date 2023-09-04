package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.flight.CreativeModuleMode;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import de.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FlightModule extends Module implements PacketListener {

    private final Value<Boolean> antiKick = new BooleanValue("Anti Kick",
            "Bypasses the vanilla fly kick.",
            this,
            false
    );

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current flight mode.",
            this,
            new CreativeModuleMode(this)
    );

    public FlightModule() {
        super(
                "Flight",
                "Allows you to fly (even in survival or adventure).",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket) {
            if (this.antiKick.getValue()) {
                playerMoveC2SPacket.y += Math.sin(player().age) * 0.2;
            }
        }
    }

}
