package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.StepListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class StepModule extends AbstractModule implements StepListener {

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
        Vandalism.getEventSystem().subscribe(StepEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(StepEvent.ID, this);
    }

    @Override
    public void onStep(final StepEvent event) {
        event.stepHeight = this.stepHeight.getValue();
    }

}
