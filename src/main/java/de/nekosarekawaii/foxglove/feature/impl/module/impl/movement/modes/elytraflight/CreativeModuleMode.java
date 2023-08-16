package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.elytraflight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.ElytraFlightModule;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class CreativeModuleMode extends ModuleMode<ElytraFlightModule> implements TickListener {

    public CreativeModuleMode(final ElytraFlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);

        final var player = mc.player;
        if (player == null)
            return;

        player.getAbilities().flying = false;
        player.getAbilities().allowFlying = player.getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        final var player = mc.player;
        if (player == null)
            return;

        if (!player.isFallFlying()) {
            player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }

        player.getAbilities().flying = true;
        player.getAbilities().allowFlying = true;
    }

}
