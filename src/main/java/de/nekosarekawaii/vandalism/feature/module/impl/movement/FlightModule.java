package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight.CreativeModuleMode;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight.CubeCraftModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FlightModule extends AbstractModule implements OutgoingPacketListener, MotionListener {

    private final BooleanValue antiKick = new BooleanValue(
            this,
            "Anti Kick",
            "Bypasses the vanilla fly kick.",
            false
    );

    private final ModuleModeValue<FlightModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current flight mode.",
            new CreativeModuleMode(this),
            new CubeCraftModuleMode(this)
    );

    private double lastPacketY = 0.0D;

    public FlightModule() {
        super("Flight", "Allows you to fly (even in survival or adventure).", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(MotionListener.MotionEvent.ID, this);
        DietrichEvents2.global().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(MotionListener.MotionEvent.ID, this);
        DietrichEvents2.global().unsubscribe(OutgoingPacketEvent.ID, this);
    }


    @Override
    public void onPreMotion(MotionEvent event) {
        if (this.mc.player == null) return;

        if (this.antiKick.getValue()) {
            this.mc.player.ticksSinceLastPositionPacketSent = 20;
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket
                && this.antiKick.getValue()
                && this.mc.player.age % 2 == 0) {
            playerMoveC2SPacket.y = playerMoveC2SPacket.y - 0.1D;
        }
    }

}
