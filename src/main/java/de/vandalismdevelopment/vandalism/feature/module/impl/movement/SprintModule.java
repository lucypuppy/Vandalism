package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationListener;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.util.MovementUtil;
import net.minecraft.util.math.MathHelper;

public class SprintModule extends AbstractModule implements MovementListener {

    private final Value<Boolean> forceSprint = new BooleanValue(
            "Force Sprint",
            "Forces you to sprint even if you are not moving.",
            this,
            false
    );

    public SprintModule() {
        super(
                "Sprint",
                "Automatically let's you sprint!",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(SprintEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(SprintEvent.ID, this);
    }

    @Override
    public void onSprint(final SprintEvent event) {
        final RotationListener rotation = Vandalism.getInstance().getRotationListener();
        final boolean useSpoofedRotation = rotation.getRotation() != null;
        if (Math.abs(MathHelper.wrapDegrees((useSpoofedRotation ? rotation.getRotation().getYaw() : mc.player.getYaw()) - MovementUtil.getInputAngle(mc.player.getYaw()))) > 45D) {
            event.sprinting = false;
            event.force = true;
            return;
        }

        event.sprinting = true;
        event.force = this.forceSprint.getValue();
    }

}
