package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.flight.CreativeModuleMode;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(name = "Flight", description = "Allows you to fly.", category = FeatureCategory.MOVEMENT)
public class FlightModule extends Module implements PacketListener {

    private final Value<Boolean> antiKick = new BooleanValue("Anti Kick", "Bypasses the vanilla fly kick.", this, false);

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current flight mode.", this,
            new CreativeModuleMode(this)
    );

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.isCancelled()) return;
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        final Packet<?> packet = event.packet;
        if (packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket) {
            if (this.antiKick.getValue()) {
                playerMoveC2SPacket.y += Math.sin(player.age) * 0.2;
            }
        }
    }

}
