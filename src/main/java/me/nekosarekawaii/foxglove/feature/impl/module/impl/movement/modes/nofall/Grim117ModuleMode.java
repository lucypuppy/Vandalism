package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.nofall;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.PacketListener;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.NoFallModule;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Grim117ModuleMode extends ModuleMode<NoFallModule> implements PacketListener, TickListener {

    public Grim117ModuleMode(final NoFallModule parent) {
        super("Grim 1.17", parent);
    }

    //https://www.youtube.com/watch?v=_quAUP9wJdE 1.17 nofall

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onWrite(final PacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket) {
            if (mc.player.fallDistance > 1.0) {

            }
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null)
            return;
    }

}
