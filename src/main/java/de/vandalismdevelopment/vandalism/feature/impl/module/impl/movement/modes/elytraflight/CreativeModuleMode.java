package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.elytraflight;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.ElytraFlightModule;
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
        if (this.mc.player == null) return;
        this.mc.player.getAbilities().flying = false;
        this.mc.player.getAbilities().allowFlying = this.mc.player.getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        if (!this.mc.player.isFallFlying()) {
            this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        this.mc.player.getAbilities().flying = true;
        this.mc.player.getAbilities().allowFlying = true;
    }

}
