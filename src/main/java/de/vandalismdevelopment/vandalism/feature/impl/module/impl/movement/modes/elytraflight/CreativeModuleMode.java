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
        if (this.player() == null) return;
        this.player().getAbilities().flying = false;
        this.player().getAbilities().allowFlying = this.player().getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (this.player() == null) return;
        if (!this.player().isFallFlying()) {
            this.networkHandler().sendPacket(new ClientCommandC2SPacket(this.player(), ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        this.player().getAbilities().flying = true;
        this.player().getAbilities().allowFlying = true;
    }

}
