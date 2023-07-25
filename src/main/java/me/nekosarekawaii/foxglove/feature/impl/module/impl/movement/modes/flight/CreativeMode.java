package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.flight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.impl.module.Mode;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.FlightModule;
import net.minecraft.client.network.ClientPlayerEntity;

public class CreativeMode extends Mode<FlightModule> implements TickListener {

    public CreativeMode(final FlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
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
        player.getAbilities().flying = true;
        player.getAbilities().allowFlying = true;
    }

}
