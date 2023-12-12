package de.vandalismdevelopment.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.FloatValue;

public class StepModule extends AbstractModule implements MovementListener {

    private final Value<Float> stepHeight = new FloatValue(
            this,
            "Step Height",
            "Allows you to customize the step height.",
            10.0f,
            0.7f,
            10.0f
    );

    public StepModule() {
        super("Step", "Changes your step height or step speed.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(StepEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(StepEvent.ID, this);
    }

    @Override
    public void onStep(final StepEvent event) {
        event.stepHeight = this.stepHeight.getValue();
    }

}
