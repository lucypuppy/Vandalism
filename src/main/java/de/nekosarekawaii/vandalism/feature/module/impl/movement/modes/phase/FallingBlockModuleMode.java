package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.phase;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;

public class FallingBlockModuleMode extends ModuleMulti<PhaseModule> implements TickGameListener {

    public FallingBlockModuleMode(final PhaseModule parent) {
        super("Falling Block", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null || this.mc.world == null) return;
        if (this.mc.player.getBlockStateAtPos().isSolid() && this.mc.world.getBlockState(this.mc.player.getBlockPos().up(1)).isSolid()) {
            final double
                    yaw = Math.toRadians(this.mc.player.headYaw),
                    horizontal = this.mc.player.forwardSpeed > 0 ? 1 : this.mc.player.forwardSpeed < 0 ? -1 : 0;

            double vertical = 0;
            if (this.mc.options.sneakKey.isPressed())
                vertical = -1;
             else if (this.mc.options.jumpKey.isPressed() && this.mc.player.fallDistance < 2.0f)
                vertical = 1;

            this.mc.player.setPos(
                    this.mc.player.getX() - Math.sin(yaw) * horizontal,
                    this.mc.player.getY() + vertical,
                    this.mc.player.getZ() + Math.cos(yaw) * horizontal
            );
        }
    }

}
