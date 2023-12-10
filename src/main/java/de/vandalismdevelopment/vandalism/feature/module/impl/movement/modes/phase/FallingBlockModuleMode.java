package de.vandalismdevelopment.vandalism.feature.module.impl.movement.modes.phase;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.template.ModuleMulti;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.PhaseModule;

public class FallingBlockModuleMode extends ModuleMulti<PhaseModule> implements TickListener {

    public FallingBlockModuleMode(final PhaseModule parent) {
        super("Falling Block", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
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
