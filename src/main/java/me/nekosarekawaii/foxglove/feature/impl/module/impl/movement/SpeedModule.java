package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.event.impl.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.EnumNameNormalizer;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.ListValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(name = "Speed", description = "Makes you faster.", category = FeatureCategory.MOVEMENT)
public class SpeedModule extends Module implements PacketListener, TickListener {

    private final Value<String> mode = new ListValue("Mode", "The current speed mode.", this, Mode.LONG_HOP.normalName());

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        if (this.mode.getValue().equals(Mode.LONG_HOP.normalName())) {
            if (player.isOnGround() && (player.forwardSpeed != 0 || player.sidewaysSpeed != 0)) {
                player.jump();
                final float yaw = (float) ((Math.atan2(player.forwardSpeed,
                        player.sidewaysSpeed) / Math.PI * 180.0F
                        + player.getYaw()
                ) * Math.PI / 180.0F);
                final double speed = 1.5;
                player.setVelocity(Math.cos(yaw) * speed, player.getVelocity().getY(), Math.sin(yaw) * speed);
            }
        }
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.isCancelled()) return;
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        final Packet<?> packet = event.packet;
        if (packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket) {

        }
    }

    private enum Mode implements EnumNameNormalizer {

        LONG_HOP;

        private final String normalName;

        Mode() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

    }

}
