package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.flight.CreativeModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.feature.module.value.ModuleModeValue;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FlightModule extends AbstractModule implements PacketListener {

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
    public void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket
                && this.antiKick.getValue())
            playerMoveC2SPacket.y += Math.sin(this.mc.player.age) * 0.2;
    }

}
