package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.event.impl.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.string.EnumNameNormalizer;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.ListValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(name = "Fly", description = "Allows you to fly.", category = FeatureCategory.MOVEMENT)
public class FlyModule extends Module implements PacketListener, TickListener {

    private final Value<Boolean> antiKick = new BooleanValue("Anti Kick", "Bypasses the vanilla fly kick.", this, false);

    private final Value<String> mode = new ListValue("Mode", "The current fly mode.", this, Mode.CREATIVE.normalName());

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        player.getAbilities().flying = false;
        player.getAbilities().allowFlying = false;
    }

    @Override
    public void onTick() {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        if (this.mode.getValue().equals(Mode.CREATIVE.normalName())) {
            player.getAbilities().flying = true;
            player.getAbilities().allowFlying = true;
        }
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

    private enum Mode implements EnumNameNormalizer {

        CREATIVE;

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
