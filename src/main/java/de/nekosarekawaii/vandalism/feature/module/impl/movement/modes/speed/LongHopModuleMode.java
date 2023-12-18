package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.speed;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;

public class LongHopModuleMode extends ModuleMulti<SpeedModule> implements TickGameListener {

    public LongHopModuleMode(final SpeedModule parent) {
        super("Long Hop", parent);
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        if (this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) {
            if (this.mc.player.isOnGround()) this.mc.player.jump();
            MovementUtil.setSpeed(1.5);
        }
    }

}
