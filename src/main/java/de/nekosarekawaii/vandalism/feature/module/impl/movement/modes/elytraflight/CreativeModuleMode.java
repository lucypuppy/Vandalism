package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.elytraflight;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class CreativeModuleMode extends ModuleMulti<ElytraFlightModule> implements TickGameListener {

    public CreativeModuleMode(final ElytraFlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
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
