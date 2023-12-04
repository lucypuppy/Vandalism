package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.MathUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationListener;

public class SprintModule extends Module implements MovementListener {
    public SprintModule() {
        super("Sprint", "Automatically let's you sprint!", FeatureCategory.MOVEMENT, false, true);
    }

    @Override
    public void onSprint(final SprintEvent event) {
        final RotationListener rotation = Vandalism.getInstance().getRotationListener();
        final boolean useSpoofedRotation = rotation.getRotation() != null;
        if (Math.abs(MathUtil.wrapAngleTo180_float((useSpoofedRotation ? rotation.getRotation().getYaw() : player().getYaw()) - MovementUtil.getInputAngle(player().getYaw()))) > 45D) {
            player().setSprinting(false);
            return;
        }
        event.bypass = false;
        event.sprinting = true;
    }

    @Override
    protected void onEnable() {
        //DietrichEvents2.global().subscribe();
        DietrichEvents2.global().subscribe(SprintEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(SprintEvent.ID, this);
    }
}
