package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.phase;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleMode;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.PhaseModule;

public class FallingBlockModuleMode extends ModuleMode<PhaseModule> implements TickListener {

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
        if (this.player() == null || this.world() == null) return;
        if (this.player().getBlockStateAtPos().isSolid() && this.world().getBlockState(this.player().getBlockPos().up(1)).isSolid()) {
            final double
                    yaw = Math.toRadians(this.player().headYaw),
                    horizontal = this.player().forwardSpeed > 0 ? 1 : this.player().forwardSpeed < 0 ? -1 : 0;

            double vertical = 0;
            if (this.options().sneakKey.isPressed())
                vertical = -1;
             else if (this.options().jumpKey.isPressed() && this.player().fallDistance < 2.0f)
                vertical = 1;

            this.player().setPos(
                    this.player().getX() - Math.sin(yaw) * horizontal,
                    this.player().getY() + vertical,
                    this.player().getZ() + Math.cos(yaw) * horizontal
            );
        }
    }

}
